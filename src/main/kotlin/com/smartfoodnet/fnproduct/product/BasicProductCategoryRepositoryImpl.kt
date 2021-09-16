package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.entity.QBasicProductCategory.basicProductCategory

class BasicProductCategoryRepositoryImpl
    : Querydsl4RepositorySupport(BasicProductCategory::class.java), BasicProductCategoryCustom {

    override fun findByLevel1CategoryAndLevel2Category(
        level1CategoryId: Long?,
        level2CategoryId: Long?,
    ): List<BasicProductCategory> {
        return selectFrom(basicProductCategory)
            .where(eqLevel1CategoryId(level1CategoryId), eqLevel2CategoryId(level2CategoryId))
            .fetch()
    }

    private fun eqLevel1CategoryId(level1CategoryId: Long?) =
        if (level1CategoryId == null) null else basicProductCategory.level1Category.id.eq(level1CategoryId)

    private fun eqLevel2CategoryId(level2CategoryId: Long?) =
        if (level2CategoryId == null) null else basicProductCategory.level2Category.id.eq(level2CategoryId)
}
