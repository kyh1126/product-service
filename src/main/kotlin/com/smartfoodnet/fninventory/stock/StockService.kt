package com.smartfoodnet.fninventory.stock

import com.smartfoodnet.apiclient.StockDefaultModel
import com.smartfoodnet.apiclient.WmsClient
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.NosnosExpirationDateStockModel
import com.smartfoodnet.apiclient.response.NosnosStockMoveEventModel
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.error.exception.UserRequestError
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
import feign.FeignException
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Service
@Transactional(readOnly = true)
class StockService(
    private val basicProductRepository: BasicProductRepository,
    private val stockByBestBeforeRepository: StockByBestBeforeRepository,
    private val orderService: OrderService,
    private val wmsClient: WmsClient
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
        val basicProductStockModels = basicProducts.map { BasicProductStockModel.fromBasicProduct(it) }

        val nosnosStocks = wmsClient.getStocks(
            partnerId = partnerId,
            shippingProductIds = basicProductStockModels.content.mapNotNull { it.shippingProductId }
        ).payload?.dataList ?: listOf()

        basicProductStockModels.forEach { model ->
            val nosnosStock = nosnosStocks.firstOrNull { it.shippingProductId == model.shippingProductId }
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
        val stockByBestBeforeModel = stocksByBestBefore.map { StockByBestBeforeModel.fromStockByBestBefore(it) }

        return PageResponse.of(stockByBestBeforeModel)
    }

    fun getStockMoveEvents(
        basicProductId: Long,
        effectiveDate: LocalDate?
    ): Any {
        val basicProduct = basicProductRepository.findByIdOrNull(basicProductId) ?: throw UserRequestError(errorMessage = "기본 상품이 존재하지 않습니다.")

        return getNosnosStockMoveEventsByWeek(basicProduct, effectiveDate?: LocalDate.now())
    }

    fun getNosnosStockMoveEventsByWeek(basicProduct: BasicProduct, effectiveDate: LocalDate): List<NosnosStockMoveEventModel> {
        val nosnosStockMoveEventModels = mutableListOf<NosnosStockMoveEventModel>()

        for (i in 1 .. 7) {
            try {
                val modelsPerDay = getNosnosStockMoveEventsByDay(basicProduct, effectiveDate.minusDays(i.toLong()))
                nosnosStockMoveEventModels.addAll(modelsPerDay)
            } catch (e: FeignException) {
                log.info("feignError: " + e.printStackTrace())
            }

        }

        return nosnosStockMoveEventModels
    }

    fun getNosnosStockMoveEventsByDay(basicProduct: BasicProduct, effectiveDate: LocalDate): List<NosnosStockMoveEventModel> {
        val nosnosResponse = getNosnosStockMovementEvents(basicProduct = basicProduct, effectiveDate = effectiveDate, page = 1)

        val totalPage = nosnosResponse?.totalPage ?: 1L

        if(totalPage == 1L) {
            return nosnosResponse?.dataList!!
        }

        val nosnosStockMoveEventModels = mutableListOf<NosnosStockMoveEventModel>()

        for (i in 2 .. totalPage.toInt()) {
            val models = getNosnosStockMovementEvents(basicProduct = basicProduct, effectiveDate = effectiveDate, page = i)?.dataList
            models?.let { nosnosStockMoveEventModels.addAll(models) }
        }

        return nosnosStockMoveEventModels
    }

    fun getNosnosStockMovementEvents(basicProduct: BasicProduct, effectiveDate: LocalDate, page: Int): CommonDataListModel<NosnosStockMoveEventModel>? {
        return wmsClient.getStocksMoveEvents(
            stockDefaultModel = StockDefaultModel(
                memberId = basicProduct.partnerId!!,
                shippingProductIds = listOf(basicProduct.shippingProductId!!.toInt()),
                page = page
            ), processDate = effectiveDate.format(DateTimeFormatter.ofPattern(Constants.NOSNOS_DATE_FORMAT))
        ).payload
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
            val nosnosStocksByExpirationDate = wmsClient.getStocksByExpirationDate(
                partnerId,
                basicProductChunk.map { it.shippingProductId } as List<Long>
            ).payload?.dataList ?: listOf()

            val stocksByBestBefore = mutableListOf<StockByBestBefore>()

            nosnosStocksByExpirationDate.forEach { nosnosStockByExpirationDate ->
                val basicProduct = basicProductChunk.find {
                    it.shippingProductId?.equals(nosnosStockByExpirationDate.shippingProductId) ?: false
                }

                if (basicProduct == null) {
                    log.error("error: shippingProductId = [${nosnosStockByExpirationDate.shippingProductId}] does not exist.")
                    return
                }

                val orderCount = orderService.getOrderCountByProductIdAndStatus(basicProduct.id!!, OrderStatus.NEW) ?: 0

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
            Duration.between(today.toLocalDate().atStartOfDay(), expirationDate.toLocalDate().atStartOfDay()).toDays()

        if (duration < 0)
            return 0f

        return duration.div(manufacturedBefore.toFloat())
    }

    companion object : Log
}