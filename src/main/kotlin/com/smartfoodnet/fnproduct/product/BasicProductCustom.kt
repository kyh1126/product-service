package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType

interface BasicProductCustom {
    fun countByPartnerIdAndTypeIn(partnerId: Long?, types: Collection<BasicProductType>): Long
}
