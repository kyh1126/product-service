package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.error.ValidatorUtils
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.error.exception.ErrorCode
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.product.entity.*
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCategoryFinder
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCodeGenerator
import com.smartfoodnet.fnproduct.product.mapper.SubsidiaryMaterialCategoryFinder
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialCreateModel
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
    private val expirationDateInfoRepository: ExpirationDateInfoRepository,
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
        val basicProductCategory = getBasicProductCategory(basicProductCreateModel)
        // (공통)부자재 카테고리 조회
        val subsidiaryMaterialCategory = getSubsidiaryMaterialCategory(basicProductCreateModel)
        // 입고처 조회
        val warehouse = getWarehouse(basicProductCreateModel)
        // 기본상품-부자재 매핑을 위한 부자재(BasicProduct) 조회
        val subsidiaryMaterialById = getSubsidiaryMaterialById(createModel)

        // 유통기한정보 저장
        val expirationDateInfo = createOrUpdateExpirationDateInfo(basicProductCreateModel)
        // 기본상품-부자재 매핑 저장
        val subsidiaryMaterials = createOrUpdateSubsidiaryMaterials(
            subsidiaryMaterialModels = createModel.subsidiaryMaterialModels,
            subsidiaryMaterialById = subsidiaryMaterialById
        )

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

    @Transactional
    fun updateBasicProduct(productId: Long, updateModel: BasicProductDetailCreateModel): BasicProductDetailModel {
        ValidatorUtils.validateAndThrow(basicProductDetailCreateModelValidator, updateModel)

        val basicProductCreateModel = updateModel.basicProductModel

        // 기본상품 카테고리 조회
        val basicProductCategory = getBasicProductCategory(basicProductCreateModel)
        // (공통)부자재 카테고리 조회
        val subsidiaryMaterialCategory = getSubsidiaryMaterialCategory(basicProductCreateModel)
        // 입고처 조회
        val warehouse = getWarehouse(basicProductCreateModel)
        // 기본상품-부자재 매핑을 위한 부자재(BasicProduct) 조회
        val subsidiaryMaterialById = getSubsidiaryMaterialById(updateModel)

        val basicProduct = basicProductRepository.findById(productId).get()

        // 유통기한정보 저장
        val expirationDateInfo =
            createOrUpdateExpirationDateInfo(basicProductCreateModel, basicProduct.expirationDateInfo)
        // 기본상품-부자재 매핑 저장
        val entityById = basicProduct.subsidiaryMaterials.associateBy { it.id }
        val subsidiaryMaterials =
            createOrUpdateSubsidiaryMaterials(updateModel.subsidiaryMaterialModels, entityById, subsidiaryMaterialById)

        basicProduct.update(
            basicProductCreateModel,
            basicProductCategory,
            subsidiaryMaterialCategory,
            expirationDateInfo,
            subsidiaryMaterials,
            warehouse
        )

        return toBasicProductDetailModel(basicProduct, subsidiaryMaterialById)
    }

    private fun getBasicProductCategory(basicProductCreateModel: BasicProductCreateModel): BasicProductCategory? {
        return (basicProductCreateModel.basicProductCategory)?.let {
            basicProductCategoryFinder.getBasicProductCategoryByKeyName(it.level1!!, it.level2!!)
        }
    }

    private fun getSubsidiaryMaterialCategory(basicProductCreateModel: BasicProductCreateModel): SubsidiaryMaterialCategory? {
        return (basicProductCreateModel.subsidiaryMaterialCategory)?.let {
            subsidiaryMaterialCategoryFinder.getSubsidiaryMaterialCategoryByKeyName(it.level1!!, it.level2!!)
        }
    }

    private fun getWarehouse(basicProductCreateModel: BasicProductCreateModel) =
        warehouseService.getWarehouse(basicProductCreateModel.warehouse.id!!)

    private fun getSubsidiaryMaterialById(createModel: BasicProductDetailCreateModel) =
        getBasicProducts(createModel.subsidiaryMaterialModels.map { it.subsidiaryMaterial.id!! })
            .associateBy { it.id }

    private fun createOrUpdateExpirationDateInfo(
        basicProductCreateModel: BasicProductCreateModel,
        entity: ExpirationDateInfo? = null,
    ): ExpirationDateInfo? {
        return (basicProductCreateModel.expirationDateInfoModel)?.let {
            if (it.id == null) it.toEntity()
            else {
                entity!!.update(it)
                entity
            }
        }
    }

    private fun createOrUpdateSubsidiaryMaterials(
        subsidiaryMaterialModels: List<SubsidiaryMaterialCreateModel>,
        entityById: Map<Long?, SubsidiaryMaterial> = emptyMap(),
        subsidiaryMaterialById: Map<Long?, BasicProduct>,
    ): List<SubsidiaryMaterial> {
        val subsidiaryMaterials = subsidiaryMaterialModels.map {
            val basicProductSub = subsidiaryMaterialById[it.subsidiaryMaterial.id]
                ?: throw BaseRuntimeException(errorCode = ErrorCode.NO_ELEMENT)
            if (it.id == null) it.toEntity(basicProductSub)
            else {
                val entity = entityById[it.id]
                entity!!.update(it, basicProductSub)
                entity
            }
        }.toMutableList()
        return subsidiaryMaterials
    }

    private fun toBasicProductDetailModel(
        basicProduct: BasicProduct,
        subsidiaryMaterialById: Map<Long?, BasicProduct>,
    ): BasicProductDetailModel {
        return BasicProductDetailModel.fromEntity(basicProduct, subsidiaryMaterialById)
    }

}
