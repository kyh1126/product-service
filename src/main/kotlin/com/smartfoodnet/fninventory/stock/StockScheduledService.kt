package com.smartfoodnet.fninventory.stock

import com.smartfoodnet.apiclient.DailyCloseStockRequestModel
import com.smartfoodnet.apiclient.DailySummaryStockRequestModel
import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.response.NosnosDailyCloseStockModel
import com.smartfoodnet.apiclient.response.NosnosDailyStockSummaryModel
import com.smartfoodnet.apiclient.response.NosnosExpirationDateStockModel
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.Constants.API_CALL_LIST_SIZE
import com.smartfoodnet.common.Constants.IS_ACTIVE_YN
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fninventory.stock.entity.DailyStockSummary
import com.smartfoodnet.fninventory.stock.entity.StockByBestBefore
import com.smartfoodnet.fninventory.stock.support.DailyStockSummaryRepository
import com.smartfoodnet.fninventory.stock.support.StockByBestBeforeRepository
import com.smartfoodnet.fnproduct.order.OrderService
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import feign.FeignException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class StockScheduledService(
    private val basicProductRepository: BasicProductRepository,
    private val stockByBestBeforeRepository: StockByBestBeforeRepository,
    private val dailyStockSummaryRepository: DailyStockSummaryRepository,
    private val orderService: OrderService,
    private val wmsApiClient: WmsApiClient
) {
    /**
     * 정해진 기간동안의 dailyStockSummary를 저장한다
     * 100일의 경우 어제부터 최근 100일간의 데이터를 nosnos에서 가져와 저장함
     * partnerId를 상품으로부터 추출한 뒤, 파트너별, 날짜별 상품 50개씩 묶어 api호출 뒤 받아온 정보를 저장함.
     */
    @Transactional
    fun saveDailyStockSummariesByDays(days: Long) {
        val partnerIds = basicProductRepository.getPartnerIdsFromBasicProduct(activeYn = IS_ACTIVE_YN)
        val today = LocalDate.now()

        for (i in days downTo 1) {
            val effectiveDate = today.minusDays(i)

            partnerIds?.forEach { partnerId ->
                val basicProducts =
                    basicProductRepository.findByPartnerIdAndActiveYnAndShippingProductIdIsNotNull(
                        partnerId,
                        IS_ACTIVE_YN
                    )
                val basicProductsChunks = basicProducts?.chunked(API_CALL_LIST_SIZE) ?: return

                basicProductsChunks.forEach { chunk ->
                    saveDailyStockSummary(
                        basicProducts = chunk,
                        partnerId = partnerId,
                        effectiveDate = effectiveDate
                    )
                }
            }
        }
    }

    @Transactional
    fun saveDailyStockSummary(
        basicProducts: List<BasicProduct>,
        partnerId: Long,
        effectiveDate: LocalDate
    ) {

        val dailyCloseStockModels: List<NosnosDailyCloseStockModel>?

        try {
            dailyCloseStockModels = wmsApiClient.getDailyCloseStock(
                DailyCloseStockRequestModel(
                    memberId = partnerId,
                    closingDate = effectiveDate.format(DateTimeFormatter.ofPattern(Constants.NOSNOS_DATE_FORMAT)),
                    shippingProductIds = basicProducts.map { it.shippingProductId!! }
                )
            ).payload?.dataList
        }catch (e:FeignException){
            return
        }

        val stockSummaryModels = try {
            wmsApiClient.getDailyStockSummary(
                DailySummaryStockRequestModel(
                    memberId = partnerId,
                    stockDate = effectiveDate.format(DateTimeFormatter.ofPattern(Constants.NOSNOS_DATE_FORMAT)),
                    shippingProductIds = basicProducts.map { it.shippingProductId!! },
                    page = 1
                )
            ).payload?.dataList
        } catch (e: Exception) {
            basicProducts.map {
                NosnosDailyStockSummaryModel(
                    shippingProductId = it.shippingProductId,
                    stockDate = effectiveDate.format(DateTimeFormatter.ofPattern(Constants.NOSNOS_DATE_FORMAT))
                )
            }
        }

        val dailyStockSummaries = mutableListOf<DailyStockSummary>()

        dailyCloseStockModels?.forEach { closeStock ->
            val summary = stockSummaryModels?.firstOrNull { it.shippingProductId == closeStock.shippingProductId }
            val basicProduct = basicProducts.firstOrNull { it.shippingProductId == closeStock.shippingProductId }!!
            val dailyStockSummary = DailyStockSummary(
                partnerId = partnerId,
                basicProduct = basicProduct,
                shippingProductId = summary?.shippingProductId ?: basicProduct.shippingProductId!!,
                inboundQuantity = summary?.receivingQuantity ?: 0,
                outboundQuantity = summary?.shipoutQuantity ?: 0,
                returnQuantity = summary?.returnQuantity ?: 0,
                outQuantity = summary?.outQuantity ?: 0,
                returnBackQuantity = summary?.returnBackQuantity ?: 0,
                returnReceiveQuantity = summary?.returnReceiveQuantity ?: 0,
                rollbackReceiveQuantity = summary?.rollbackReceiveQuantity ?: 0,
                adjustInQuantity = summary?.adjustInQuantity ?: 0,
                adjustOutQuantity = summary?.adjustOutQuantity ?: 0,
                totalStockCount = closeStock.totalQuantity ?: 0,
                availableStockCount = closeStock.normalStock ?: 0,
                effectiveDate = effectiveDate
            )

            dailyStockSummary.calculateTotalStockChange()
            dailyStockSummaries.add(dailyStockSummary)
        }

        dailyStockSummaryRepository.saveAll(dailyStockSummaries)
    }

    @Transactional
    fun syncStocksByBestBefore() {
        val partnerIds =
            basicProductRepository.getPartnerIdsFromBasicProduct(
                Constants.EXPIRATION_DATE_MANAGEMENT_YN,
                IS_ACTIVE_YN
            )

        partnerIds?.forEach { syncStocksByBestBeforeByPartner(it) }
    }

    private fun syncStocksByBestBeforeByPartner(partnerId: Long) {
        val basicProducts =
            basicProductRepository.findByPartnerIdAndExpirationDateManagementYnAndActiveYn(
                partnerId,
                Constants.EXPIRATION_DATE_MANAGEMENT_YN,
                IS_ACTIVE_YN
            )
        val basicProductsChunks = basicProducts?.chunked(API_CALL_LIST_SIZE) ?: return

        basicProductsChunks.forEach { basicProductChunk ->
            val nosnosStocksByExpirationDate = wmsApiClient.getStocksByExpirationDate(
                partnerId,
                basicProductChunk.map { it.shippingProductId } as List<Long>
            ).payload?.dataList ?: listOf()

            val stocksByBestBefore = mutableListOf<StockByBestBefore>()

            nosnosStocksByExpirationDate.forEach { nosnosStockByExpirationDate ->
                val basicProduct = basicProductChunk.find {
                    it.shippingProductId?.equals(nosnosStockByExpirationDate.shippingProductId)
                        ?: false
                }

                if (basicProduct == null) {
                    StockService.log.error("error: shippingProductId = [${nosnosStockByExpirationDate.shippingProductId}] does not exist.")
                    return
                }

                val orderCount = orderService.getOrderCountByProductIdAndStatus(
                    basicProduct.id!!,
                    OrderStatus.NEW
                ) ?: 0

                val stockByBestBefore = buildStockByBestBefore(
                    basicProduct,
                    nosnosStockByExpirationDate,
                    orderCount
                )

                stocksByBestBefore.add(stockByBestBefore)
            }

            stockByBestBeforeRepository.saveAll(stocksByBestBefore)
        }
    }

    private fun buildStockByBestBefore(
        basicProduct: BasicProduct,
        nosnosStockByExpirationDate: NosnosExpirationDateStockModel,
        orderCount: Int
    ): StockByBestBefore {
        val manufacturedDate = basicProduct.expirationDateInfo?.expirationDate?.toLong()?.let {
            nosnosStockByExpirationDate.expirationDate?.minusDays(it)
        }

        return StockByBestBefore(
            partnerId = basicProduct.partnerId!!,
            basicProduct = basicProduct,
            shippingProductId = basicProduct.shippingProductId!!,
            bestBefore = calculateBestBefore(
                nosnosStockByExpirationDate.expirationDate!!,
                basicProduct.expirationDateInfo!!.expirationDate!!
            ),
            manufactureDate = manufacturedDate,
            expirationDate = nosnosStockByExpirationDate.expirationDate,
            totalStockCount = nosnosStockByExpirationDate.totalStock,
            availableStockCount = nosnosStockByExpirationDate.normalStock,
            totalNewOrdersCount = orderCount,
            collectedDate = LocalDate.now()
        )
    }

    private fun calculateBestBefore(expirationDate: LocalDateTime, manufacturedBefore: Int): Float {
        val today = LocalDateTime.now()
        val duration =
            Duration.between(
                today.toLocalDate().atStartOfDay(),
                expirationDate.toLocalDate().atStartOfDay()
            ).toDays()

        if (duration < 0)
            return 0f

        return duration.div(manufacturedBefore.toFloat())
    }

    companion object : Log
}