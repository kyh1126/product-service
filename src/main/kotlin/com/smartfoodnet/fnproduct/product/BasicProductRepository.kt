package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface BasicProductRepository : JpaRepository<BasicProduct, Long>, BasicProductCustom,
        QuerydslPredicateExecutor<BasicProduct> {
    fun findAllByIdIn(ids: Collection<Long>): List<BasicProduct>

    fun findByCode(code: String): BasicProduct?
    fun findByPartnerIdAndName(partnerId: Long, name: String): BasicProduct?
    fun findByPartnerIdAndExpirationDateManagementYnAndActiveYn(
            partnerId: Long,
            expirationDateManagementYn: String,
            activeYn: String
    ): List<BasicProduct>

    fun findByPartnerIdAndActiveYnAndShippingProductIdIsNotNull(
            partnerId: Long,
            activeYn: String
    ): List<BasicProduct>

    fun findByShippingProductId(shippingProductId: Long): BasicProduct?
    fun findAllByShippingProductIdIn(shippingProductIds: Collection<Long>): List<BasicProduct>
    fun findAllByPartnerId(partnerId: Long): List<BasicProduct>
}
