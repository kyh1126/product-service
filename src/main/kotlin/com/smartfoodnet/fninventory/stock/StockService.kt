package com.smartfoodnet.fnproduct.stock

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.stock.model.BasicProductStockModel
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StockService(
    private val basicProductRepository: BasicProductRepository
) {
    fun getBasicProductStocks(condition: PredicateSearchCondition, page: Pageable) {
        val basicProducts = basicProductRepository.findAll(condition.toPredicate(), page).content
        val basicProductStockModels = basicProducts.map { BasicProductStockModel.fromBasicProduct(it) }



    }
}