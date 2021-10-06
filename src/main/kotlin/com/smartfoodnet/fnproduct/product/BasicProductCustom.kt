package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BasicProductCustom {
    fun findByPartnerIdAndType(partnerId: Long, type: BasicProductType?, page: Pageable): Page<BasicProduct>

    fun countByPartnerIdAndTypeIn(partnerId: Long?, types: Collection<BasicProductType>): Long
}
