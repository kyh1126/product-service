package com.smartfoodnet.fnproduct.store.model

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.store.entity.QStoreProduct.storeProduct

class StoreProductPredicate(
    val partnerId : Long,
    private val storeProductName : String? = null,
    private val storeProductCode : String? = null,
    private val storeProductOptionName : String? = null
) : PredicateSearchCondition(){
    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            storeProduct.partnerId.eq(partnerId),
            storeProductName?.let { storeProduct.name.eq(it) },
            storeProductCode?.let { storeProduct.storeProductCode.eq(it) },
            storeProductOptionName?.let { storeProduct.optionName.eq(it) }
        )
    }
}
