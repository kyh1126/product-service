package com.smartfoodnet.fninventory.stock

import com.smartfoodnet.apiclient.StockApiClient
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fninventory.stock.entity.StockByBestBefore
import com.smartfoodnet.fninventory.stock.model.BasicProductStockModel
import com.smartfoodnet.fninventory.stock.model.StockByBestBeforeModel
import com.smartfoodnet.fninventory.stock.support.StockByBestBeforeRepository
import com.smartfoodnet.fninventory.stock.support.StockByBestBeforeSearchCondition
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime


@Service
@Transactional(readOnly = true)
class StockService(
        private val basicProductRepository: BasicProductRepository,
        private val stockByBestBeforeRepository: StockByBestBeforeRepository,
        private val stockApiClient: StockApiClient
) {
    //유통기한 관리 여부
    private val EXPIRATION_DATEMANAGEMENT_YN = "Y"

    //nosnos api call List Size
    private val API_CALL_LIST_SIZE = 50

    fun getBasicProductStocks(
            partnerId: Long,
            condition: PredicateSearchCondition,
            page: Pageable
    ): PageResponse<BasicProductStockModel> {
        val basicProducts = basicProductRepository.findAll(condition.toPredicate(), page)
        val basicProductStockModels = basicProducts.map { BasicProductStockModel.fromBasicProduct(it) }

        val nosnosStocks = stockApiClient.getStocks(
                partnerId = partnerId,
                shippingProductIds = basicProductStockModels.content.map { it.shippingProductId }
        )

        basicProductStockModels.forEach { model ->
            val nosnosStock = nosnosStocks?.firstOrNull { it.shippingProductId == model.shippingProductId }
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

    //TODO: 추후 상품 개수가 많아질 경우 파트너별로 잘라서 작업 고려
    fun syncStocksByBestBefore(partnerId: Long) {
        val basicProducts = basicProductRepository.findByExpirationDateManagementYnAndActiveYn(EXPIRATION_DATEMANAGEMENT_YN, EXPIRATION_DATEMANAGEMENT_YN)
        val arrBasicPoducts = basicProducts?.chunked(API_CALL_LIST_SIZE) ?: return

        arrBasicPoducts.forEach { basicProductChunk ->
            val shippingProductIds = basicProductChunk.map { it.shippingProductId }

            val nosnosStocksByExpirationDate = stockApiClient.getStocksByExpirationDate(
                    shippingProductIds as List<Long>
            )

            val stocksByBestBefore = mutableListOf<StockByBestBefore>()

            nosnosStocksByExpirationDate?.forEach{ nosnosStockByExpirationDate ->
                val basicProduct = basicProductChunk.find { it.shippingProductId?.equals(nosnosStockByExpirationDate.shippingProductId) ?: false }

                nosnosStockByExpirationDate.stocksByExpirationDate?.forEach { stockByExpirationDate ->
//                    val stockByBestBefore = StockByBestBefore(
//                            partnerId = partnerId,
//                            basicProduct = basicProduct!!,
//                            shippingProductId = nosnosStockByExpirationDate.shippingProductId!!,
//                            bestBefore = null,
//                            manufactureDate = basicProduct.expirationDateInfo.expirationDate
//                    )
                }
            }

        }
    }

    private fun calculateBestBefore(expirationDate: LocalDateTime, manufacturedBefore: Int): Float {
        val today = LocalDateTime.now()
        val duration = Duration.between(today, expirationDate).toDays()

        return duration.div(manufacturedBefore.toFloat())
    }
}