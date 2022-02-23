package com.smartfoodnet.fnproduct.store.support

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.store.entity.QStoreProduct.storeProduct
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import com.smartfoodnet.fnproduct.store.model.StoreProductPredicate

class StoreProductRepositoryImpl : StoreProductRepositoryCustom, Querydsl4RepositorySupport(StoreProduct::class.java) {
    override fun findStoreProduct(condition: StoreProductPredicate): StoreProduct? {
        return selectFrom(storeProduct)
            .where( condition.toPredicate() )
            .fetchOne()
    }
}