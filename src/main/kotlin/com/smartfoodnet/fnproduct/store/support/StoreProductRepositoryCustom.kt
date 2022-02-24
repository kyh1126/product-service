package com.smartfoodnet.fnproduct.store.support

import com.smartfoodnet.fnproduct.store.entity.StoreProduct

interface StoreProductRepositoryCustom {
    fun findStoreProduct(condition: StoreProductSearchCondition) : StoreProduct?
}