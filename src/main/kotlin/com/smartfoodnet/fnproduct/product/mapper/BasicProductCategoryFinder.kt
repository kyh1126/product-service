package com.smartfoodnet.fnproduct.product.mapper

import com.smartfoodnet.fnproduct.product.BasicProductCategoryRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
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
    ): List<BasicProductCategory> {
        return basicProductCategoryRepository.findByLevel1CategoryAndLevel2Category(
            level1CategoryId,
            level2CategoryId
        )
    }

    fun getBasicProductCategory(id: Long): BasicProductCategory {
        return basicProductCategoryRepository.findById(id).get()
    }
}
