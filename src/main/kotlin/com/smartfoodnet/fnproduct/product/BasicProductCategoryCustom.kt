package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory

interface BasicProductCategoryCustom {
    fun findByLevel1CategoryAndLevel2Category(
        level1CategoryName: String?,
        level2CategoryName: String?,
    ): List<BasicProductCategory>
}
