package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.error.ValidatorUtils
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCategoryFinder
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCodeGenerator
import com.smartfoodnet.fnproduct.product.mapper.SubsidiaryMaterialCategoryFinder
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductDetailModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.validator.BasicProductDetailCreateModelValidator
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BasicProductService(
    private val warehouseService: WarehouseService,
    private val basicProductRepository: BasicProductRepository,
    private val basicProductDetailCreateModelValidator: BasicProductDetailCreateModelValidator,
    private val basicProductCategoryFinder: BasicProductCategoryFinder,
    private val subsidiaryMaterialCategoryFinder: SubsidiaryMaterialCategoryFinder,
    private val basicProductCodeGenerator: BasicProductCodeGenerator,
) {

    fun getBasicProducts(
        partnerId: Long,
        type: BasicProductType? = null,
        page: Pageable,
    ): PageResponse<BasicProductModel> {
        return basicProductRepository.findByPartnerIdAndType(partnerId, type, page)
            .map(BasicProductModel::fromEntity)
            .run { PageResponse.of(this) }
    }

    fun getBasicProducts(ids: List<Long>): List<BasicProduct> {
        return basicProductRepository.findAllById(ids)
    }

    fun getBasicProduct(productId: Long): BasicProductDetailModel {
        val basicProduct = basicProductRepository.findById(productId).get()
        // 기본상품-부자재 매핑을 위한 부자재(BasicProduct) 조회
        val subsidiaryMaterialById =
            getBasicProducts(basicProduct.subsidiaryMaterials.map { it.subsidiaryMaterial.id!! })
                .associateBy { it.id }

        return basicProduct.run {
            toBasicProductDetailModel(this, subsidiaryMaterialById)
        }
    }

    fun getBasicProductCategories(level1CategoryId: Long?, level2CategoryId: Long?): List<CategoryByLevelModel> {
        return basicProductCategoryFinder.getBasicProductCategories(level1CategoryId, level2CategoryId)
    }

    fun getSubsidiaryMaterialCategories(
        level1CategoryId: Long?,
        level2CategoryId: Long?,
    ): List<CategoryByLevelModel> {
        return subsidiaryMaterialCategoryFinder.getSubsidiaryMaterialCategories(level1CategoryId, level2CategoryId)
    }

    @Transactional
    fun createBasicProduct(createModel: BasicProductDetailCreateModel): BasicProductDetailModel {
        ValidatorUtils.validateAndThrow(basicProductDetailCreateModelValidator, createModel)

        val basicProductCreateModel = createModel.basicProductModel
        // 상품코드 채번
        val basicProductCode = with(basicProductCreateModel) {
            basicProductCodeGenerator.getBasicProductCode(partnerId, type, handlingTemperature?.code)
        }
        // 기본상품 카테고리 조회
        val basicProductCategory = (basicProductCreateModel.basicProductCategory)?.let {
            basicProductCategoryFinder.getBasicProductCategoryByKeyName(it.level1!!, it.level2!!)
        }
        // (공통)부자재 카테고리 조회
        val subsidiaryMaterialCategory = (basicProductCreateModel.subsidiaryMaterialCategory)?.let {
            subsidiaryMaterialCategoryFinder.getSubsidiaryMaterialCategoryByKeyName(it.level1!!, it.level2!!)
        }
        // 입고처 조회
        val warehouse = warehouseService.getWarehouse(basicProductCreateModel.warehouse!!.id!!)
        // 기본상품-부자재 매핑을 위한 부자재(BasicProduct) 조회
        val subsidiaryMaterialById =
            getBasicProducts(createModel.subsidiaryMaterialModels.map { it.subsidiaryMaterial.id!! })
                .associateBy { it.id }

        // 유통기한정보 저장
        val expirationDateInfo = basicProductCreateModel.expirationDateInfoModel?.toEntity()
        // 기본상품-부자재 매핑 저장
        val subsidiaryMaterials = createModel.subsidiaryMaterialModels.mapNotNull {
            if (!subsidiaryMaterialById.containsKey(it.subsidiaryMaterial.id)) null
            else it.toEntity(subsidiaryMaterialById[it.subsidiaryMaterial.id]!!)
        }.toMutableList()

        val basicProduct = createModel.toEntity(
            code = basicProductCode,
            basicProductCategory = basicProductCategory,
            subsidiaryMaterialCategory = subsidiaryMaterialCategory,
            expirationDateInfo = expirationDateInfo,
            subsidiaryMaterials = subsidiaryMaterials,
            warehouse = warehouse
        )
        basicProductRepository.save(basicProduct)

        return toBasicProductDetailModel(basicProduct, subsidiaryMaterialById)
    }

    private fun toBasicProductDetailModel(
        basicProduct: BasicProduct,
        subsidiaryMaterialById: Map<Long?, BasicProduct>,
    ): BasicProductDetailModel {
        return BasicProductDetailModel.fromEntity(basicProduct, subsidiaryMaterialById)
    }

}
