package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderModel
import com.smartfoodnet.fnproduct.order.dto.QCollectedOrderModel
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmOrder.confirmOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmPackageProduct.confirmPackageProduct
import com.smartfoodnet.fnproduct.order.entity.QConfirmProduct.confirmProduct
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ConfirmOrderRepositoryImpl : ConfirmOrderRepositoryCustom, Querydsl4RepositorySupport(ConfirmOrder::class.java) {
    override fun findAllByCondition(condition: PredicateSearchCondition, page: Pageable): Page<CollectedOrderModel> {
        return applyPagination(page){
            it.select(
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
        }
    }
}