package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderModel
import com.smartfoodnet.fnproduct.order.dto.ConfirmProductModel
import com.smartfoodnet.fnproduct.order.dto.QCollectedOrderModel
import com.smartfoodnet.fnproduct.order.dto.QConfirmProductModel
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.*
import com.smartfoodnet.fnproduct.order.entity.QConfirmProduct
import com.smartfoodnet.fnproduct.order.entity.QConfirmProduct.*
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.*
import com.smartfoodnet.fnproduct.store.entity.QStoreProduct.storeProduct

class ConfirmProductRepositoryImpl : ConfirmProductRepositoryCustom, Querydsl4RepositorySupport(
    ConfirmProduct::class.java){
    override fun findAllCollectedOrderWithConfirmProduct(condition: PredicateSearchCondition): List<ConfirmProductModel> {
        return select(
            QConfirmProductModel(
                collectedOrder.id,
                confirmProduct.id,
                collectedOrder.partnerId,
                collectedOrder.uploadType,
                collectedOrder.status,
                collectedOrder.orderNumber,
                collectedOrder.orderedAt,
                collectedOrder.bundleNumber,
                confirmProduct.matchingType,
                basicProduct.id,
                basicProduct.code,
                basicProduct.type,
                basicProduct.salesProductId,
                basicProduct.salesProductCode,
                basicProduct.shippingProductId,
                basicProduct.productCode,
                basicProduct.name,
                confirmProduct.quantity,
                confirmProduct.quantityPerUnit,
                collectedOrder.storeId,
                collectedOrder.storeName,
                collectedOrder.collectedProductInfo.collectedStoreProductCode,
                collectedOrder.collectedProductInfo.collectedStoreProductName,
                collectedOrder.collectedProductInfo.collectedStoreProductOptionName,
                collectedOrder.collectedProductInfo.collectedStoreProductOptionCode,
                storeProduct.id,
                storeProduct.name,
                storeProduct.storeProductCode,
                storeProduct.optionName,
                storeProduct.optionCode,
                collectedOrder.quantity,
                collectedOrder.deliveryType,
                collectedOrder.shippingPrice,
                collectedOrder.receiver.name,
                collectedOrder.receiver.zipCode,
                collectedOrder.receiver.address,
                collectedOrder.receiver.phoneNumber,
                collectedOrder.collectedAt
            )
        )
            .from(collectedOrder)
            .leftJoin(collectedOrder.confirmProductList, confirmProduct)
            .leftJoin(collectedOrder.storeProduct, storeProduct)
            .leftJoin(confirmProduct.basicProduct, basicProduct)
            .where(condition.toPredicate())
            .fetch()
    }
}