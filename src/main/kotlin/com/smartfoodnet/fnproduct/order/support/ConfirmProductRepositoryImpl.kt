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
                collectedOrder.bundleNumber,
                confirmProduct.matchingType,
                basicProduct.id,
                basicProduct.type,
                basicProduct.salesProductId,
                basicProduct.salesProductCode,
                basicProduct.shippingProductId,
                basicProduct.productCode,
                basicProduct.name,
                confirmProduct.quantityPerUnit,
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
            .from(collectedOrder)
            .leftJoin(
                collectedOrder.confirmProductList,
                confirmProduct
            )
            .leftJoin(confirmProduct.basicProduct, basicProduct)
            .where(condition.toPredicate())
            .fetch()
    }
}