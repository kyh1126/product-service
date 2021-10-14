package com.smartfoodnet.fnproduct.product.mapper

import com.smartfoodnet.fnproduct.code.CodeService
import com.smartfoodnet.fnproduct.product.BasicProductCategoryRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class BasicProductCategoryFinder(
    private val codeService: CodeService,
    private val basicProductCategoryRepository: BasicProductCategoryRepository,
) {
    private val level1GroupName = "basic_product_category.level_1_category"
    private val level2GroupName = "basic_product_category.level_2_category"

    fun getBasicProductCategories(
        level1CategoryId: Long? = null,
        level2CategoryId: Long? = null,
    ): List<CategoryByLevelModel> {
        return basicProductCategoryRepository.findByLevel1CategoryAndLevel2Category(level1CategoryId, level2CategoryId)
            .groupBy({ it.level1Category }, { it.level2Category })
            .map { CategoryByLevelModel.fromEntity(it.key, it.value) }
    }

    fun getBasicProductCategoryByKeyName(level1: String, level2: String): BasicProductCategory? {
        val level1Code = codeService.getCodeByGroupNameKeyName(level1GroupName, level1).first()
        val level2Code = codeService.getCodeByGroupNameKeyName(level2GroupName, level2).first()

        return basicProductCategoryRepository.findByLevel1CategoryAndLevel2Category(level1Code.id, level2Code.id)
            .firstOrNull()
    }
}
