package com.smartfoodnet.fnproduct.order.support

import com.querydsl.core.types.Projections
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fninventory.shortage.model.ShortageOrderProjectionModel
import com.smartfoodnet.fninventory.shortage.support.ProductShortageSearchCondition
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderFlatModel
import com.smartfoodnet.fnproduct.order.dto.QBasicProductFlatModel
import com.smartfoodnet.fnproduct.order.dto.QCollectedOrderFlatModel
import com.smartfoodnet.fnproduct.order.dto.QStoreProductFlatModel
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmOrder.confirmOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmRequestOrder.confirmRequestOrder
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import com.smartfoodnet.fnproduct.product.entity.QPackageProductMapping.packageProductMapping
import com.smartfoodnet.fnproduct.store.entity.QStoreProduct.storeProduct
import com.smartfoodnet.fnproduct.store.entity.QStoreProductMapping.storeProductMapping
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class CollectedOrderRepositoryImpl : CollectedOrderRepositoryCustom, Querydsl4RepositorySupport(
    CollectedOrder::class.java
) {

    override fun findAllByPartnerIdAndStatusGroupByProductId(
        partnerId: Long,
        status: OrderStatus,
        condition: ProductShortageSearchCondition
    ): List<ShortageOrderProjectionModel> {

        return select(
            Projections.fields(
                ShortageOrderProjectionModel::class.java,
                basicProduct.id.`as`("basicProductId"),
                basicProduct.name.`as`("basicProductName"),
                basicProduct.code.`as`("basicProductCode"),
                basicProduct.id.count().`as`("shortageOrderCount"),
                basicProduct.shippingProductId,
                (collectedOrder.quantity.sum()).multiply(storeProductMapping.quantity).`as`("totalOrderCount"),
                collectedOrder.price.sum().`as`("totalShortagePrice")
            )
        ).from(collectedOrder)
            .innerJoin(collectedOrder.storeProduct.storeProductMappings, storeProductMapping)
            .innerJoin(storeProductMapping.basicProduct, basicProduct)
            .on(collectedOrder.status.eq(status).and(collectedOrder.partnerId.eq(partnerId)))
            .where(condition.toPredicate())
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

    override fun findCollectedOrders(
        condition: PredicateSearchCondition,
        page: Pageable
    ): Page<CollectedOrderFlatModel> {
        return applyPagination(page) {
            it.select(buildQCollectedOrderProjection())
                .from(collectedOrder)
                .leftJoin(collectedOrder.storeProduct, storeProduct)
                .leftJoin(storeProduct.storeProductMappings, storeProductMapping)
                .leftJoin(storeProductMapping.basicProduct, basicProduct)
                .leftJoin(collectedOrder.confirmRequestOrder, confirmRequestOrder)
                .leftJoin(confirmRequestOrder.confirmOrder, confirmOrder)
                .where(condition.toPredicate())
        }
    }

    override fun findMissingAffectedOrders(
        partnerId: Long,
        basicProductId: Long
    ): List<CollectedOrder> {
        return selectFrom(collectedOrder)
            .innerJoin(collectedOrder.storeProduct, storeProduct)
            .innerJoin(storeProduct.storeProductMappings, storeProductMapping)
            .innerJoin(storeProductMapping.basicProduct, basicProduct)
            .leftJoin(basicProduct.packageProductMappings, packageProductMapping)
            .leftJoin(packageProductMapping.selectedBasicProduct)
            .where(
                collectedOrder.status.eq(OrderStatus.NEW),
                packageProductMapping.selectedBasicProduct.id.eq(basicProductId)
                    .or(storeProductMapping.basicProduct.id.eq(basicProductId))
            )
            .fetch()
    }

    private fun buildQCollectedOrderProjection(): QCollectedOrderFlatModel {
        return QCollectedOrderFlatModel(
            collectedOrder.id,
            collectedOrder.partnerId,
            collectedOrder.uploadType,
            collectedOrder.status,
            collectedOrder.orderNumber,
            confirmOrder.orderCode,
            collectedOrder.bundleNumber,
            collectedOrder.storeId,
            collectedOrder.storeName,
            collectedOrder.collectedProductInfo.collectedStoreProductCode,
            collectedOrder.collectedProductInfo.collectedStoreProductName,
            collectedOrder.collectedProductInfo.collectedStoreProductOptionName,
            collectedOrder.quantity,
            QStoreProductFlatModel(
                storeProduct.id,
                storeProduct.name,
                storeProduct.storeProductCode,
                storeProduct.optionName,
                storeProduct.optionCode
            ),
            QBasicProductFlatModel(
                basicProduct.id,
                basicProduct.type.stringValue(),
                basicProduct.name,
                basicProduct.salesProductId,
                basicProduct.salesProductCode,
                basicProduct.shippingProductId,
                basicProduct.productCode,
                storeProductMapping.quantity,
            ),
            collectedOrder.deliveryType,
            collectedOrder.shippingPrice,
            collectedOrder.receiver.name,
            collectedOrder.receiver.address,
            collectedOrder.receiver.phoneNumber,
            collectedOrder.collectedAt
        )
    }
}