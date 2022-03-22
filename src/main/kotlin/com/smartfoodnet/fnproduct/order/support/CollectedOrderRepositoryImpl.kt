package com.smartfoodnet.fnproduct.order.support

import com.querydsl.core.types.Projections
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fninventory.shortage.model.ShortageOrderProjectionModel
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderModel
import com.smartfoodnet.fnproduct.order.dto.QCollectedOrderModel
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import com.smartfoodnet.fnproduct.store.entity.QStoreProduct.storeProduct
import com.smartfoodnet.fnproduct.store.entity.QStoreProductMapping.storeProductMapping

class CollectedOrderRepositoryImpl : CollectedOrderRepositoryCustom, Querydsl4RepositorySupport(
    CollectedOrder::class.java) {
    override fun findAllByPartnerIdAndStatusGroupByProductId(
        partnerId: Long,
        status: OrderStatus
    ): List<ShortageOrderProjectionModel> {

        return select(
            Projections.fields(
            ShortageOrderProjectionModel::class.java,
            basicProduct.id.`as`("basicProductId"),
            basicProduct.name.`as`("basicProductName"),
            basicProduct.code.`as`("basicProductCode"),
            basicProduct.id.count().`as`("shortageOrderCount"),
            basicProduct.shippingProductId,
            collectedOrder.quantity.sum().`as`("totalOrderCount"),
            collectedOrder.price.sum().`as`("totalShortagePrice")
        )).from(collectedOrder)
            .innerJoin(collectedOrder.storeProduct.storeProductMappings, storeProductMapping)
            .innerJoin(storeProductMapping.basicProduct, basicProduct)
            .on(collectedOrder.status.eq(status).and(collectedOrder.partnerId.eq(partnerId)))
            .groupBy(basicProduct.shippingProductId, basicProduct.id)
            .fetch()
    }

    override fun getCountByProductIdAndStatusGroupByProductId(productId: Long, status: OrderStatus): Int? {
        return select(
            collectedOrder.quantity.sum()
        ).from(collectedOrder)
            .innerJoin(collectedOrder.storeProduct.storeProductMappings, storeProductMapping)
            .innerJoin(storeProductMapping.basicProduct, basicProduct)
            .where(collectedOrder.status.eq(status).and(basicProduct.id.eq(productId)))
            .groupBy(basicProduct.id)
            .fetchOne()
    }

    override fun findAllCollectedOrders(condition: PredicateSearchCondition): List<CollectedOrderModel> {
        return select(
            QCollectedOrderModel(
                collectedOrder.id,
                collectedOrder.partnerId,
                collectedOrder.uploadType,
                collectedOrder.status,
                collectedOrder.orderNumber,
                collectedOrder.bundleNumber,
                basicProduct.id,
                basicProduct.type.stringValue(),
                basicProduct.salesProductId,
                basicProduct.salesProductCode,
                basicProduct.shippingProductId,
                basicProduct.productCode,
                basicProduct.name,
                storeProductMapping.quantity,
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
            .leftJoin(collectedOrder.storeProduct, storeProduct)
            .leftJoin(storeProduct.storeProductMappings, storeProductMapping)
            .leftJoin(storeProductMapping.basicProduct, basicProduct)
            .where(condition.toPredicate())
            .fetch()
    }
}