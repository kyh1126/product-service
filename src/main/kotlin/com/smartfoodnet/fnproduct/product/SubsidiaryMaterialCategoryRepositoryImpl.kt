package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.code.entity.QCode.code
import com.smartfoodnet.fnproduct.product.entity.QSubsidiaryMaterialCategory.subsidiaryMaterialCategory
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory

class SubsidiaryMaterialCategoryRepositoryImpl
    : Querydsl4RepositorySupport(SubsidiaryMaterialCategory::class.java),
    SubsidiaryMaterialCategoryCustom {

    override fun findByLevel1CategoryAndLevel2Category(
        level1CategoryName: String?,
        level2CategoryName: String?,
    ): List<SubsidiaryMaterialCategory> {
        return selectFrom(subsidiaryMaterialCategory)
            .innerJoin(subsidiaryMaterialCategory.level1Category, code).fetchJoin()
            .leftJoin(subsidiaryMaterialCategory.level2Category, code).fetchJoin()
            .where(
                eqLevel1CategoryName(level1CategoryName),
                eqLevel2CategoryName(level2CategoryName)
            )
            .fetch()
    }

    private fun eqLevel1CategoryName(level1CategoryName: String?) =
        level1CategoryName?.let { subsidiaryMaterialCategory.level1Category.keyName.eq(it) }

    private fun eqLevel2CategoryName(level2CategoryName: String?) =
        level2CategoryName?.let { subsidiaryMaterialCategory.level2Category.keyName.eq(it) }
}
