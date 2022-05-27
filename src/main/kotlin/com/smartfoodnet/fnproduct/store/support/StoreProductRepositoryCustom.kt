package com.smartfoodnet.fnproduct.store.support

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface StoreProductRepositoryCustom {
    fun findStoreProduct(condition: PredicateSearchCondition) : StoreProduct?
    fun findStoreProducts(condition: PredicateSearchCondition, page: Pageable) : Page<StoreProduct>
    fun findFlattenedStoreProducts(condition: PredicateSearchCondition, page: Pageable): Page<StoreProduct>
}