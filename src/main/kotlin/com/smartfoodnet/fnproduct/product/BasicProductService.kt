package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.apiclient.MessageApiClient
import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.common.error.ValidatorUtils
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.error.exception.ErrorCode
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialMapping
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCategoryFinder
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCodeGenerator
import com.smartfoodnet.fnproduct.product.mapper.BasicProductFinder
import com.smartfoodnet.fnproduct.product.mapper.SubsidiaryMaterialCategoryFinder
import com.smartfoodnet.fnproduct.product.model.dto.CategoryDto
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialMappingCreateModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductDetailModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import com.smartfoodnet.fnproduct.product.validator.BasicProductDetailCreateModelValidator
import com.smartfoodnet.fnproduct.warehouse.InWarehouseService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BasicProductService(
    private val inWarehouseService: InWarehouseService,
    private val basicProductRepository: BasicProductRepository,
    private val basicProductDetailCreateModelValidator: BasicProductDetailCreateModelValidator,
    private val basicProductFinder: BasicProductFinder,
    private val basicProductCategoryFinder: BasicProductCategoryFinder,
    private val subsidiaryMaterialCategoryFinder: SubsidiaryMaterialCategoryFinder,
    private val basicProductCodeGenerator: BasicProductCodeGenerator,
    private val messageApiClient: MessageApiClient,
) {

    fun getBasicProducts(
        condition: PredicateSearchCondition,
        page: Pageable
    ): PageResponse<BasicProductModel> {
        return basicProductRepository.findAll(condition.toPredicate(), page)
            .map(BasicProductModel::fromEntity)
            .run { PageResponse.of(this) }
    }

    fun getBasicProducts(ids: List<Long>): List<BasicProduct> {
        return basicProductRepository.findAllById(ids)
    }

    fun getBasicProductSubs(
        condition: PredicateSearchCondition,
        page: Pageable
    ): List<CategoryByLevelModel> {
        val basicProductSubIdsByCategoryId = getBasicProducts(condition, page).payload
            .groupBy({ it.subsidiaryMaterialCategory!!.id!! }, { it.id })

        // level2 카테고리에 기본상품 id 를 넣어준다.
        return getSubsidiaryMaterialCategories().map {
            it.children.map {
                it.value = basicProductSubIdsByCategoryId[it.value]?.first()
            }
            it
        }
    }

    fun getBasicProduct(productId: Long): BasicProductDetailModel {
        val basicProduct = getBasicProducts(listOf(productId)).first()
        // 기본상품-부자재 매핑을 위한 부자재(BasicProduct) 조회
        val subsidiaryMaterialById =
            getBasicProducts(basicProduct.subsidiaryMaterialMappings.map { it.subsidiaryMaterial.id!! })
                .associateBy { it.id }

        return toBasicProductDetailModel(basicProduct, subsidiaryMaterialById)
    }

    fun getBasicProductCategories(
        level1CategoryName: String?,
        level2CategoryName: String?
    ): List<CategoryByLevelModel> {
        val basicProductCategories = basicProductCategoryFinder.getBasicProductCategories(
            level1CategoryName,
            level2CategoryName
        )

        // level2 카테고리에 기본상품 카테고리의 id 를 넣어준다.
        return basicProductCategories.groupBy(
            { it.level1Category },
            { CategoryDto.fromEntity(it.id, it.level2Category) }
        ).run { toCategoriesByLevelModel(this) }
    }

    fun getSubsidiaryMaterialCategories(
        level1CategoryName: String? = null,
        level2CategoryName: String? = null,
    ): List<CategoryByLevelModel> {
        val subsidiaryMaterialCategories =
            subsidiaryMaterialCategoryFinder.getSubsidiaryMaterialCategories(
                level1CategoryName,
                level2CategoryName
            )

        // level2 카테고리에 기본상품 카테고리의 id 를 넣어준다.
        return subsidiaryMaterialCategories.groupBy(
            { it.level1Category },
            { CategoryDto.fromEntity(it.id, it.level2Category) }
        ).run { toCategoriesByLevelModel(this) }
    }

    @Transactional
    fun createBasicProduct(createModel: BasicProductDetailCreateModel): BasicProductDetailModel {
        ValidatorUtils.validateAndThrow(basicProductDetailCreateModelValidator, createModel)

        val basicProductCreateModel = createModel.basicProductModel
        // 상품코드 채번
        val basicProductCode = with(basicProductCreateModel) {
            basicProductCodeGenerator.getBasicProductCode(partnerId, type, handlingTemperature)
        }
        // 기본상품 카테고리 조회
        val basicProductCategory = getBasicProductCategory(basicProductCreateModel)
        // (공통)부자재 카테고리 조회
        val subsidiaryMaterialCategory = getSubsidiaryMaterialCategory(basicProductCreateModel)
        // 입고처 조회
        val inWarehouse = getWarehouse(basicProductCreateModel)
        // 기본상품-부자재 매핑을 위한 부자재(BasicProduct) 조회
        val subsidiaryMaterialById = getSubsidiaryMaterialById(createModel)

        // 기본상품-부자재 매핑 저장
        val subsidiaryMaterialMappings = createOrUpdateSubsidiaryMaterialMappings(
            subsidiaryMaterialMappingModels = createModel.subsidiaryMaterialMappingModels,
            subsidiaryMaterialById = subsidiaryMaterialById
        )

        val basicProduct = createModel.toEntity(
            code = basicProductCode,
            basicProductCategory = basicProductCategory,
            subsidiaryMaterialCategory = subsidiaryMaterialCategory,
            subsidiaryMaterialMappings = subsidiaryMaterialMappings,
            inWarehouse = inWarehouse
        )

        return saveBasicProduct(basicProduct)
            .also {
//                messageApiClient.sendMessage(
//                    destination = SfnTopic.PRODUCT_CREATED,
//                    message = BasicProductCreatedModel(it.id!!)
//                )

                // TODO: nosnos 쪽 POST request 하기
            }.run {
                toBasicProductDetailModel(this, subsidiaryMaterialById)
            }
    }

    @Transactional
    fun updateBasicProduct(
        productId: Long,
        updateModel: BasicProductDetailCreateModel
    ): BasicProductDetailModel {
        ValidatorUtils.validateAndThrow(
            SaveState.UPDATE,
            basicProductDetailCreateModelValidator,
            updateModel
        )

        val basicProductCreateModel = updateModel.basicProductModel

        // 기본상품 카테고리 조회
        val basicProductCategory = getBasicProductCategory(basicProductCreateModel)
        // (공통)부자재 카테고리 조회
        val subsidiaryMaterialCategory = getSubsidiaryMaterialCategory(basicProductCreateModel)
        // 입고처 조회
        val inWarehouse = getWarehouse(basicProductCreateModel)
        // 기본상품-부자재 매핑을 위한 부자재(BasicProduct) 조회
        val subsidiaryMaterialById = getSubsidiaryMaterialById(updateModel)

        val basicProduct = getBasicProducts(listOf(productId)).first()

        // 기본상품-부자재 매핑 저장
        val entityById = basicProduct.subsidiaryMaterialMappings.associateBy { it.id }
        val subsidiaryMaterials = createOrUpdateSubsidiaryMaterialMappings(
            updateModel.subsidiaryMaterialMappingModels,
            entityById,
            subsidiaryMaterialById
        )

        // 모음상품-기본상품의 기본상품이 비활성화 될 경우, 모음상품도 비활성화
        inactivatePackageProduct(basicProductCreateModel, basicProduct)

        basicProduct.update(
            basicProductCreateModel,
            basicProductCategory,
            subsidiaryMaterialCategory,
            subsidiaryMaterials,
            inWarehouse
        )

        return toBasicProductDetailModel(basicProduct, subsidiaryMaterialById)
    }

    @Transactional
    fun saveBasicProduct(basicProduct: BasicProduct): BasicProduct {
        return basicProductRepository.save(basicProduct)
    }

    private fun getBasicProductCategory(basicProductCreateModel: BasicProductCreateModel): BasicProductCategory? {
        return (basicProductCreateModel.basicProductCategoryId)?.let {
            basicProductCategoryFinder.getBasicProductCategory(it)
        }
    }

    private fun getSubsidiaryMaterialCategory(basicProductCreateModel: BasicProductCreateModel): SubsidiaryMaterialCategory? {
        return (basicProductCreateModel.subsidiaryMaterialCategoryId)?.let {
            subsidiaryMaterialCategoryFinder.getSubsidiaryMaterialCategory(it)
        }
    }

    private fun getWarehouse(basicProductCreateModel: BasicProductCreateModel) =
        inWarehouseService.getInWarehouse(basicProductCreateModel.warehouseId!!)

    private fun getSubsidiaryMaterialById(createModel: BasicProductDetailCreateModel) =
        getBasicProducts(createModel.subsidiaryMaterialMappingModels.map { it.subsidiaryMaterialId!! })
            .associateBy { it.id }

    private fun createOrUpdateSubsidiaryMaterialMappings(
        subsidiaryMaterialMappingModels: List<SubsidiaryMaterialMappingCreateModel>,
        entityById: Map<Long?, SubsidiaryMaterialMapping> = emptyMap(),
        subsidiaryMaterialById: Map<Long?, BasicProduct>,
    ): Set<SubsidiaryMaterialMapping> {
        val subsidiaryMaterialMappings = subsidiaryMaterialMappingModels.map {
            val basicProductSub = subsidiaryMaterialById[it.subsidiaryMaterialId]
                ?: throw BaseRuntimeException(errorCode = ErrorCode.NO_ELEMENT)
            if (it.id == null) it.toEntity(basicProductSub)
            else {
                val entity = entityById[it.id]
                entity!!.update(it, basicProductSub)
                entity
            }
        }.run { LinkedHashSet(this) }

        // 연관관계 끊긴 entity 삭제처리
        entityById.values.toSet().minus(subsidiaryMaterialMappings)
            .forEach(SubsidiaryMaterialMapping::delete)

        return subsidiaryMaterialMappings
    }

    private fun inactivatePackageProduct(
        request: BasicProductCreateModel,
        basicProduct: BasicProduct
    ) {
        if (request.activeYn == "Y") return

        basicProductFinder.getPackageProductByBasicProduct(basicProduct.id!!)?.let {
            if (it.activeYn == "N") return
            it.inactivate()
            log.info("기본상품(${basicProduct.id})에 의한 모음상품(${it.id}) 비활성화")
        }
    }

    private fun toBasicProductDetailModel(
        basicProduct: BasicProduct,
        subsidiaryMaterialById: Map<Long?, BasicProduct>,
    ): BasicProductDetailModel {
        return BasicProductDetailModel.fromEntity(basicProduct, subsidiaryMaterialById)
    }

    private fun toCategoriesByLevelModel(categoriesByLevel1: Map<Code, List<CategoryDto?>>) =
        categoriesByLevel1.map { CategoryByLevelModel.fromEntity(it.key, it.value) }

    companion object : Log
}
