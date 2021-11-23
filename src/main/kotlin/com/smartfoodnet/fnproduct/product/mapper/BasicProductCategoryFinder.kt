package com.smartfoodnet.fnproduct.product.mapper

import com.smartfoodnet.fnproduct.product.BasicProductCategoryRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class BasicProductCategoryFinder(
    private val basicProductCategoryRepository: BasicProductCategoryRepository,
) {
    fun getBasicProductCategories(
        level1CategoryId: Long? = null,
        level2CategoryId: Long? = null,
    ): List<CategoryByLevelModel> {
        return basicProductCategoryRepository.findByLevel1CategoryAndLevel2Category(
            level1CategoryId,
            level2CategoryId
        ).groupBy({ it.level1Category }, { it.level2Category })
            .map { CategoryByLevelModel.fromEntity(it.key, it.value) }
    }

    fun getBasicProductCategory(id: Long): BasicProductCategory {
        return basicProductCategoryRepository.findById(id).get()
    }
}
