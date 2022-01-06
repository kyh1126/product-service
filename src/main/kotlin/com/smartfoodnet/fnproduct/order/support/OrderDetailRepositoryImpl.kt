package com.smartfoodnet.fnproduct.order.support

import com.querydsl.core.types.Projections
import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fninventory.shortage.model.ShortageOrderProjectionModel
import com.smartfoodnet.fnproduct.order.entity.OrderDetail
import com.smartfoodnet.fnproduct.order.entity.QOrderDetail.orderDetail
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct

class OrderDetailRepositoryImpl : Querydsl4RepositorySupport(OrderDetail::class.java), OrderDetailCustom {
    override fun findAllByPartnerIdAndStatusGroupByProductId(
        partnerId: Long,
        status: OrderStatus
    ): List<ShortageOrderProjectionModel> {

        return select(Projections.fields(
            ShortageOrderProjectionModel::class.java,
            basicProduct.id.`as`("basicProductId"),
            basicProduct.name.`as`("basicProductName"),
            basicProduct.code.`as`("basicProductCode"),
            basicProduct.id.count().`as`("shortageOrderCount"),
            basicProduct.shippingProductId,
            orderDetail.count.sum().`as`("totalOrderCount"),
            orderDetail.price.sum().`as`("totalShortagePrice")
        )).from(orderDetail)
            .innerJoin(orderDetail.storeProduct.basicProduct ,basicProduct)
            .on(orderDetail.status.eq(status).and(orderDetail.partnerId.eq(partnerId)))
            .groupBy(basicProduct.shippingProductId, basicProduct.id)
            .fetch()
    }

    override fun getCountByProductIdAndStatusGroupByProductId(productId: Long, status: OrderStatus): Int? {
        return select(
            orderDetail.count.sum()
        ).from(orderDetail)
            .innerJoin(orderDetail.storeProduct.basicProduct, basicProduct)
            .where(orderDetail.status.eq(status).and(basicProduct.id.eq(productId)))
            .groupBy(basicProduct.id)
            .fetchOne()
    }
}