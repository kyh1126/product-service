package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.code.entity.QCode.code
import com.smartfoodnet.fnproduct.product.entity.QSubsidiaryMaterialCategory.subsidiaryMaterialCategory
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory

class SubsidiaryMaterialCategoryRepositoryImpl
    : Querydsl4RepositorySupport(SubsidiaryMaterialCategory::class.java),
    SubsidiaryMaterialCategoryCustom {

    override fun findByLevel1CategoryAndLevel2Category(
        level1CategoryId: Long?,
        level2CategoryId: Long?,
    ): List<SubsidiaryMaterialCategory> {
        return selectFrom(subsidiaryMaterialCategory)
            .innerJoin(subsidiaryMaterialCategory.level1Category, code).fetchJoin()
            .leftJoin(subsidiaryMaterialCategory.level2Category, code).fetchJoin()
            .where(eqLevel1CategoryId(level1CategoryId), eqLevel2CategoryId(level2CategoryId))
            .fetch()
    }

    private fun eqLevel1CategoryId(level1CategoryId: Long?) =
        level1CategoryId?.let { subsidiaryMaterialCategory.level1Category.id.eq(it) }

    private fun eqLevel2CategoryId(level2CategoryId: Long?) =
        level2CategoryId?.let { subsidiaryMaterialCategory.level2Category.id.eq(it) }
}
