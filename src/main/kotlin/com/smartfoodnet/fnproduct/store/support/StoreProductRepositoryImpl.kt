package com.smartfoodnet.fnproduct.store.support

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import com.smartfoodnet.fnproduct.store.entity.QStoreProduct.storeProduct
import com.smartfoodnet.fnproduct.store.entity.QStoreProductMapping.storeProductMapping
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
            selectFrom(storeProduct)
                .leftJoin(storeProduct.storeProductMappings, storeProductMapping).fetchJoin()
                .leftJoin(storeProductMapping.basicProduct, basicProduct).fetchJoin()
                .where( condition.toPredicate() )
                .distinct()
        }
    }

    override fun findFlattenedStoreProducts(condition: PredicateSearchCondition, page: Pageable): Page<StoreProduct> {
        return applyPagination(page) {
            it.selectFrom(storeProduct)
                .leftJoin(storeProduct.storeProductMappings, storeProductMapping).fetchJoin()
                .leftJoin(storeProductMapping.basicProduct, basicProduct).fetchJoin()
                .where( condition.toPredicate() )
        }
    }
}