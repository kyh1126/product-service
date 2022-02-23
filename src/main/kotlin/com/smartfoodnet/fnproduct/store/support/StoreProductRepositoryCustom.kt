package com.smartfoodnet.fnproduct.store.support

import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import com.smartfoodnet.fnproduct.store.model.StoreProductPredicate

interface StoreProductRepositoryCustom {
    fun findStoreProduct(condition: StoreProductPredicate) : StoreProduct?
}