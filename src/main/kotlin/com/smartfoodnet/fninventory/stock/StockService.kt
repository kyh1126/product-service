package com.smartfoodnet.fninventory.stock

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.response.NosnosExpirationDateStockModel
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fninventory.stock.entity.StockByBestBefore
import com.smartfoodnet.fninventory.stock.model.BasicProductStockModel
import com.smartfoodnet.fninventory.stock.model.StockByBestBeforeModel
import com.smartfoodnet.fninventory.stock.support.StockByBestBeforeRepository
import com.smartfoodnet.fninventory.stock.support.StockByBestBeforeSearchCondition
import com.smartfoodnet.fnproduct.order.OrderService
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime


@Service
@Transactional(readOnly = true)
class StockService(
    private val basicProductRepository: BasicProductRepository,
    private val stockByBestBeforeRepository: StockByBestBeforeRepository,
    private val wmsApiClient: WmsApiClient,
    private val orderService: OrderService,
) {
    //유통기한 관리 여부
    private val EXPIRATION_DATEMANAGEMENT_YN = "Y"

    //활성화 여부
    private val IS_ACTIVE_YN = "Y"

    //nosnos api call List Size
    private val API_CALL_LIST_SIZE = 50

    fun getBasicProductStocks(
        partnerId: Long,
        condition: PredicateSearchCondition,
        page: Pageable
    ): PageResponse<BasicProductStockModel> {
        val basicProducts = basicProductRepository.findAll(condition.toPredicate(), page)
        val basicProductStockModels =
            basicProducts.map { BasicProductStockModel.fromBasicProduct(it) }

        val nosnosStocks = wmsApiClient.getStocks(
            partnerId = partnerId,
            shippingProductIds = basicProductStockModels.content.mapNotNull { it.shippingProductId }
        ).payload?.dataList ?: listOf()

        basicProductStockModels.forEach { model ->
            val nosnosStock =
                nosnosStocks.firstOrNull { it.shippingProductId == model.shippingProductId }
            nosnosStock?.let { model.fillInNosnosStockValues(nosnosStock) }
        }

        return PageResponse.of(basicProductStockModels)
    }

    fun getStocksByBestBefore(
        partnerId: Long,
        condition: StockByBestBeforeSearchCondition,
        page: Pageable
    ): PageResponse<StockByBestBeforeModel> {
        val stocksByBestBefore = stockByBestBeforeRepository.findAll(condition.toPredicate(), page)
        val stockByBestBeforeModel =
            stocksByBestBefore.map { StockByBestBeforeModel.fromStockByBestBefore(it) }

        return PageResponse.of(stockByBestBeforeModel)
    }

    fun syncStocksByBestBefore() {
        val partnerIds =
            basicProductRepository.getPartnerIdsFromBasicProduct(
                EXPIRATION_DATEMANAGEMENT_YN,
                IS_ACTIVE_YN
            )

        partnerIds?.forEach { syncStocksByBestBeforeByPartner(it) }
    }

    fun syncStocksByBestBeforeByPartner(partnerId: Long) {
        val basicProducts =
            basicProductRepository.findByPartnerIdAndExpirationDateManagementYnAndActiveYn(
                partnerId,
                EXPIRATION_DATEMANAGEMENT_YN,
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
                    log.error("error: shippingProductId = [${nosnosStockByExpirationDate.shippingProductId}] does not exist.")
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
