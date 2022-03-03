package com.smartfoodnet.fnproduct.store.support

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct
import com.smartfoodnet.fnproduct.store.entity.QStoreProduct.storeProduct
import com.smartfoodnet.fnproduct.store.entity.QStoreProductMapping
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class StoreProductRepositoryImpl : StoreProductRepositoryCustom, Querydsl4RepositorySupport(StoreProduct::class.java) {
    override fun findStoreProduct(condition: PredicateSearchCondition): StoreProduct? {
        return selectFrom(storeProduct)
            .where( condition.toPredicate() )
            .fetchOne()
    }

    override fun findStoreProducts(condition: PredicateSearchCondition, page: Pageable): Page<StoreProduct> {
        return applyPagination(page) {
            it.selectFrom(storeProduct)
                .leftJoin(storeProduct.storeProductMappings, QStoreProductMapping.storeProductMapping)
                .leftJoin(QStoreProductMapping.storeProductMapping.basicProduct, QBasicProduct.basicProduct)
                .where( condition.toPredicate() )
                .distinct()
        }
    }
}