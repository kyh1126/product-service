package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.response.BasicProductDetailModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BasicProductService(
    private val basicProductRepository: BasicProductRepository,
    private val basicProductCategoryRepository: BasicProductCategoryRepository,
    private val subsidiaryMaterialCategoryRepository: SubsidiaryMaterialCategoryRepository,
) {

    fun getBasicProducts(partnerId: Long): List<BasicProductModel> {
        return basicProductRepository.findByPartnerId(partnerId).map { toBasicProductModel(it) }
    }

    fun getBasicProduct(productId: Long): BasicProductDetailModel {
        return basicProductRepository.findById(productId).get().run {
            toBasicProductDetailModel(this)
        }
    }

    fun getBasicProductCategories(level1CategoryId: Long?, level2CategoryId: Long?): List<CategoryByLevelModel> {
        return basicProductCategoryRepository.findByLevel1CategoryAndLevel2Category(level1CategoryId, level2CategoryId)
            .groupBy({ it.level1Category }, { it.level2Category })
            .map { CategoryByLevelModel.fromEntity(it.key, it.value) }
    }

    fun getSubsidiaryMaterialCategories(
        level1CategoryId: Long?,
        level2CategoryId: Long?,
    ): List<CategoryByLevelModel> {
        return subsidiaryMaterialCategoryRepository
            .findByLevel1CategoryAndLevel2Category(level1CategoryId, level2CategoryId)
            .groupBy({ it.level1Category }, { it.level2Category })
            .map { CategoryByLevelModel.fromEntity(it.key, it.value) }
    }

    private fun toBasicProductModel(basicProduct: BasicProduct): BasicProductModel {
        return BasicProductModel.fromEntity(basicProduct)
    }

    private fun toBasicProductDetailModel(basicProduct: BasicProduct): BasicProductDetailModel {
        return BasicProductDetailModel.fromEntity(basicProduct)
    }
}
