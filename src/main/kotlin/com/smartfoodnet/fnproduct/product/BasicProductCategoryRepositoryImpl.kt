package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.code.entity.QCode.code
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.entity.QBasicProductCategory.basicProductCategory

class BasicProductCategoryRepositoryImpl
    : Querydsl4RepositorySupport(BasicProductCategory::class.java), BasicProductCategoryCustom {

    override fun findByLevel1CategoryAndLevel2Category(
        level1CategoryId: Long?,
        level2CategoryId: Long?,
    ): List<BasicProductCategory> {
        return selectFrom(basicProductCategory)
            .innerJoin(basicProductCategory.level1Category, code).fetchJoin()
            .leftJoin(basicProductCategory.level2Category, code).fetchJoin()
            .where(eqLevel1CategoryId(level1CategoryId), eqLevel2CategoryId(level2CategoryId))
            .fetch()
    }

    private fun eqLevel1CategoryId(level1CategoryId: Long?) =
        level1CategoryId?.let { basicProductCategory.level1Category.id.eq(it) }

    private fun eqLevel2CategoryId(level2CategoryId: Long?) =
        level2CategoryId?.let { basicProductCategory.level2Category.id.eq(it) }
}
