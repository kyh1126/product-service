package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.code.entity.QCode.code
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.entity.QBasicProductCategory.basicProductCategory

class BasicProductCategoryRepositoryImpl
    : Querydsl4RepositorySupport(BasicProductCategory::class.java), BasicProductCategoryCustom {

    override fun findByLevel1CategoryAndLevel2Category(
        level1CategoryName: String?,
        level2CategoryName: String?,
    ): List<BasicProductCategory> {
        return selectFrom(basicProductCategory)
            .innerJoin(basicProductCategory.level1Category, code).fetchJoin()
            .leftJoin(basicProductCategory.level2Category, code).fetchJoin()
            .where(
                eqLevel1CategoryName(level1CategoryName),
                eqLevel2CategoryName(level2CategoryName)
            )
            .fetch()
    }

    private fun eqLevel1CategoryName(level1CategoryName: String?) =
        level1CategoryName?.let { basicProductCategory.level1Category.keyName.eq(it) }

    private fun eqLevel2CategoryName(level2CategoryName: String?) =
        level2CategoryName?.let { basicProductCategory.level2Category.keyName.eq(it) }
}
