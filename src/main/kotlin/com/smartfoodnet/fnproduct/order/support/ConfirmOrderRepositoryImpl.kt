package com.smartfoodnet.fnproduct.order.support

import com.querydsl.jpa.impl.JPAQuery
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderModel
import com.smartfoodnet.fnproduct.order.dto.QCollectedOrderModel
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmOrder.confirmOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmProduct.confirmProduct
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import com.smartfoodnet.fnproduct.store.entity.QStoreProduct.storeProduct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ConfirmOrderRepositoryImpl : ConfirmOrderRepositoryCustom, Querydsl4RepositorySupport(ConfirmOrder::class.java) {
    override fun findAllByConfirmOrderWithPageable(condition: PredicateSearchCondition, page: Pageable): Page<CollectedOrderModel> {
        return applyPagination(page){
            createConfirmOrderQuery(condition)
        }
    }

    override fun findAllByConfirmOrder(condition: PredicateSearchCondition): List<CollectedOrderModel> {
        return createConfirmOrderQuery(condition).fetch()
    }

    private fun createConfirmOrderQuery(condition : PredicateSearchCondition) : JPAQuery<CollectedOrderModel>{
        return select(
            QCollectedOrderModel(
                collectedOrder.id,
                collectedOrder.partnerId,
                collectedOrder.uploadType,
                collectedOrder.status,
                collectedOrder.orderNumber,
                collectedOrder.bundleNumber,
                basicProduct.id,
                basicProduct.salesProductId,
                basicProduct.salesProductCode,
                basicProduct.shippingProductId,
                basicProduct.productCode,
                basicProduct.name,
                confirmProduct.quantity,
                collectedOrder.storeId,
                collectedOrder.storeName,
                collectedOrder.collectedProductInfo.collectedStoreProductCode,
                collectedOrder.collectedProductInfo.collectedStoreProductName,
                collectedOrder.collectedProductInfo.collectedStoreProductOptionName,
                collectedOrder.storeProduct.id,
                collectedOrder.storeProduct.name,
                collectedOrder.storeProduct.storeProductCode,
                collectedOrder.storeProduct.optionName,
                collectedOrder.storeProduct.optionCode,
                collectedOrder.quantity,
                collectedOrder.deliveryType,
                collectedOrder.shippingPrice,
                collectedOrder.receiver.name,
                collectedOrder.receiver.address,
                collectedOrder.receiver.phoneNumber,
                collectedOrder.collectedAt
            )
        )
            .from(confirmOrder)
            .leftJoin(confirmOrder.confirmProductList, confirmProduct)
            .leftJoin(confirmProduct.basicProduct, basicProduct)
            .leftJoin(confirmProduct.collectedOrder, collectedOrder)
            .leftJoin(collectedOrder.storeProduct, storeProduct)
        .where(condition.toPredicate())
    }
}