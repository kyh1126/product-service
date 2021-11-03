package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.common.error.ValidatorUtils
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.error.exception.ErrorCode
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCodeGenerator
import com.smartfoodnet.fnproduct.product.model.request.PackageProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.PackageProductMappingCreateModel
import com.smartfoodnet.fnproduct.product.model.request.PackageProductMappingSearchCondition
import com.smartfoodnet.fnproduct.product.model.response.PackageProductDetailModel
import com.smartfoodnet.fnproduct.product.model.response.PackageProductMappingDetailModel
import com.smartfoodnet.fnproduct.product.validator.PackageProductDetailCreateModelValidator
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PackageProductService(
    private val basicProductService: BasicProductService,
    private val packageProductMappingRepository: PackageProductMappingRepository,
    private val packageProductDetailCreateModelValidator: PackageProductDetailCreateModelValidator,
    private val basicProductCodeGenerator: BasicProductCodeGenerator,
) {

    fun getPackageProducts(
        condition: PackageProductMappingSearchCondition,
        page: Pageable
    ): PageResponse<PackageProductMappingDetailModel> {
        return packageProductMappingRepository.findAllByCondition(condition, page)
            .map(PackageProductMappingDetailModel::fromEntity)
            .run { PageResponse.of(this) }
    }

    fun getPackageProduct(productId: Long): PackageProductDetailModel {
        val packageProduct = basicProductService.getBasicProducts(listOf(productId)).first()
        // 모음상품-기본상품 매핑을 위한 기본상품(BasicProduct) 조회
        val basicProductById =
            basicProductService.getBasicProducts(packageProduct.packageProductMappings.map { it.selectedBasicProduct.id!! })
                .associateBy { it.id }

        return toPackageProductDetailModel(packageProduct, basicProductById)
    }

    @Transactional
    fun createPackageProduct(createModel: PackageProductDetailCreateModel): PackageProductDetailModel {
        ValidatorUtils.validateAndThrow(packageProductDetailCreateModelValidator, createModel)

        val packageProductModel = createModel.packageProductModel
        // 상품코드 채번
        val basicProductCode = with(packageProductModel) {
            basicProductCodeGenerator.getBasicProductCode(partnerId, type, handlingTemperature)
        }
        // 모음상품-기본상품 매핑을 위한 기본상품(BasicProduct) 조회
        val basicProductById = getBasicProductById(createModel)

        // 모음상품-기본상품 매핑 저장
        val packageProductMappings = createOrUpdatePackageProductMappings(
            packageProductMappingModels = createModel.packageProductMappingModels,
            basicProductById = basicProductById
        )

        val basicProduct = createModel.toEntity(
            code = basicProductCode,
            packageProductMappings = packageProductMappings
        )
        return basicProductService.saveBasicProduct(basicProduct)
            .run { toPackageProductDetailModel(this, basicProductById) }
    }

    @Transactional
    fun updatePackageProduct(
        productId: Long,
        updateModel: PackageProductDetailCreateModel
    ): PackageProductDetailModel {
        ValidatorUtils.validateAndThrow(
            SaveState.UPDATE,
            packageProductDetailCreateModelValidator,
            updateModel
        )

        // 모음상품-기본상품 매핑을 위한 기본상품(BasicProduct) 조회
        val basicProductById = getBasicProductById(updateModel)

        val packageProduct = basicProductService.getBasicProducts(listOf(productId)).first()

        // 모음상품-기본상품 매핑 저장
        val entityById = packageProduct.packageProductMappings.associateBy { it.id }
        val packageProductMappings = createOrUpdatePackageProductMappings(
            updateModel.packageProductMappingModels,
            entityById,
            basicProductById
        )

        packageProduct.update(updateModel.packageProductModel, packageProductMappings)

        return toPackageProductDetailModel(packageProduct, basicProductById)
    }

    private fun getBasicProductById(createModel: PackageProductDetailCreateModel) =
        basicProductService.getBasicProducts(createModel.packageProductMappingModels.map { it.basicProductModel.id!! })
            .associateBy { it.id }

    private fun createOrUpdatePackageProductMappings(
        packageProductMappingModels: List<PackageProductMappingCreateModel>,
        entityById: Map<Long?, PackageProductMapping> = emptyMap(),
        basicProductById: Map<Long?, BasicProduct>,
    ): Set<PackageProductMapping> {
        val packageProductMappings = packageProductMappingModels.map {
            val selectedBasicProduct = basicProductById[it.basicProductModel.id]
                ?: throw BaseRuntimeException(errorCode = ErrorCode.NO_ELEMENT)
            if (it.id == null) it.toEntity(selectedBasicProduct)
            else {
                val entity = entityById[it.id]
                entity!!.update(it, selectedBasicProduct)
                entity
            }
        }.run { LinkedHashSet(this) }

        // 연관관계 끊긴 entity 삭제처리
        entityById.values.toSet().minus(packageProductMappings)
            .forEach(PackageProductMapping::delete)

        return packageProductMappings
    }

    private fun toPackageProductDetailModel(
        packageProduct: BasicProduct,
        basicProductById: Map<Long?, BasicProduct>,
    ) = PackageProductDetailModel.fromEntity(packageProduct, basicProductById)

}
