package com.smartfoodnet.fninventory.stock

import com.smartfoodnet.apiclient.StockApiClient
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fninventory.stock.model.BasicProductStockModel
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional(readOnly = true)
class StockService(
    private val basicProductRepository: BasicProductRepository,
    private val stockApiClient: StockApiClient
) {
    fun getBasicProductStocks(
        partnerId: Long,
        condition: PredicateSearchCondition,
        page: Pageable
    ): PageResponse<BasicProductStockModel> {
        //TODO check how we are going to map memberId and partnerId (are they the same?)
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
}