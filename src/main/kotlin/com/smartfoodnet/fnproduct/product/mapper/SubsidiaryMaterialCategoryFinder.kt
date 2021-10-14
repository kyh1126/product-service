package com.smartfoodnet.fnproduct.product.mapper

import com.smartfoodnet.fnproduct.code.CodeService
import com.smartfoodnet.fnproduct.product.SubsidiaryMaterialCategoryRepository
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class SubsidiaryMaterialCategoryFinder(
    private val codeService: CodeService,
    private val subsidiaryMaterialCategoryRepository: SubsidiaryMaterialCategoryRepository,
) {
    private val level1GroupName = "subsidiary_material_category.level_1_category"
    private val level2GroupName = "subsidiary_material_category.level_2_category"

    fun getSubsidiaryMaterialCategories(
        level1CategoryId: Long? = null,
        level2CategoryId: Long? = null,
    ): List<CategoryByLevelModel> {
        return subsidiaryMaterialCategoryRepository.findByLevel1CategoryAndLevel2Category(level1CategoryId,
            level2CategoryId)
            .groupBy({ it.level1Category }, { it.level2Category })
            .map { CategoryByLevelModel.fromEntity(it.key, it.value) }
    }

    fun getSubsidiaryMaterialCategoryByKeyName(level1: String, level2: String): SubsidiaryMaterialCategory? {
        val level1Code = codeService.getCodeByGroupNameKeyName(level1GroupName, level1).first()
        val level2Code = codeService.getCodeByGroupNameKeyName(level2GroupName, level2).first()

        return subsidiaryMaterialCategoryRepository.findByLevel1CategoryAndLevel2Category(level1Code.id, level2Code.id)
            .firstOrNull()
    }
}
