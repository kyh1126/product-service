package com.smartfoodnet.fnproduct.product.mapper

import com.smartfoodnet.fnproduct.product.SubsidiaryMaterialCategoryRepository
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class SubsidiaryMaterialCategoryFinder(
    private val subsidiaryMaterialCategoryRepository: SubsidiaryMaterialCategoryRepository,
) {
    fun getSubsidiaryMaterialCategories(
        level1CategoryId: Long? = null,
        level2CategoryId: Long? = null,
    ): List<CategoryByLevelModel> {
        return subsidiaryMaterialCategoryRepository.findByLevel1CategoryAndLevel2Category(
            level1CategoryId,
            level2CategoryId
        ).groupBy({ it.level1Category }, { it.level2Category })
            .map { CategoryByLevelModel.fromEntity(it.key, it.value) }
    }

    fun getSubsidiaryMaterialCategory(id: Long): SubsidiaryMaterialCategory {
        return subsidiaryMaterialCategoryRepository.findById(id).get()
    }
}
