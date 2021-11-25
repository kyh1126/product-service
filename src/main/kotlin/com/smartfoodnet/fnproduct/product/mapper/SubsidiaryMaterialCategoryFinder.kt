package com.smartfoodnet.fnproduct.product.mapper

import com.smartfoodnet.fnproduct.product.SubsidiaryMaterialCategoryRepository
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class SubsidiaryMaterialCategoryFinder(
    private val subsidiaryMaterialCategoryRepository: SubsidiaryMaterialCategoryRepository,
) {
    fun getSubsidiaryMaterialCategories(
        level1CategoryName: String? = null,
        level2CategoryName: String? = null,
    ): List<SubsidiaryMaterialCategory> {
        return subsidiaryMaterialCategoryRepository.findByLevel1CategoryAndLevel2Category(
            level1CategoryName,
            level2CategoryName
        )
    }

    fun getSubsidiaryMaterialCategory(id: Long): SubsidiaryMaterialCategory {
        return subsidiaryMaterialCategoryRepository.findById(id).get()
    }
}
