package com.smartfoodnet.fnproduct.store.support

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.store.entity.QStoreProduct.storeProduct
import com.smartfoodnet.fnproduct.store.entity.StoreProduct

class StoreProductRepositoryImpl : StoreProductRepositoryCustom, Querydsl4RepositorySupport(StoreProduct::class.java) {
    override fun findStoreProduct(condition: StoreProductSearchCondition): StoreProduct? {
        return selectFrom(storeProduct)
            .where( condition.toPredicate() )
            .fetchOne()
    }
}