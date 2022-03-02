package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmOrder.confirmOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmPackageProduct.confirmPackageProduct
import com.smartfoodnet.fnproduct.order.entity.QConfirmProduct.confirmProduct

class ConfirmOrderRepositoryImpl : ConfirmOrderRepositoryCustom, Querydsl4RepositorySupport(ConfirmOrder::class.java) {
    override fun findAllByCondition(): List<ConfirmOrder> {
        return selectFrom(confirmOrder)
            .leftJoin(confirmOrder.confirmProductList, confirmProduct).fetchJoin()
            .leftJoin(confirmProduct.confirmPackageProductList, confirmPackageProduct).fetchJoin()
            .leftJoin(confirmProduct.collectedOrder, collectedOrder).fetchJoin()
            .distinct()
            .fetch()
    }
}