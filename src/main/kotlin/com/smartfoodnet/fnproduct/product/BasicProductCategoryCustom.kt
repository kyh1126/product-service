package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory

interface BasicProductCategoryCustom {
    fun findByLevel1CategoryAndLevel2Category(
        level1CategoryId: Long?,
        level2CategoryId: Long?,
    ): List<BasicProductCategory>
}
