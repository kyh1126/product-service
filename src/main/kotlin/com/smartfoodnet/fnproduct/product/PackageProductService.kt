package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.error.ValidatorUtils
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.error.exception.ErrorCode
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCodeGenerator
import com.smartfoodnet.fnproduct.product.model.request.PackageProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.PackageProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.PackageProductMappingSearchCondition
import com.smartfoodnet.fnproduct.product.model.response.PackageProductDetailModel
import com.smartfoodnet.fnproduct.product.model.response.PackageProductMappingModel
import com.smartfoodnet.fnproduct.product.validator.PackageProductDetailCreateModelValidator
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PackageProductService(
    private val basicProductService: BasicProductService,
    private val basicProductRepository: BasicProductRepository,
    private val packageProductMappingRepository: PackageProductMappingRepository,
    private val packageProductDetailCreateModelValidator: PackageProductDetailCreateModelValidator,
    private val basicProductCodeGenerator: BasicProductCodeGenerator,
) {

    fun getPackageProducts(
        condition: PackageProductMappingSearchCondition,
        page: Pageable
    ): PageResponse<PackageProductMappingModel> {
        return packageProductMappingRepository.findAllByCondition(condition, page)
            .map(PackageProductMappingModel::fromEntity)
            .run { PageResponse.of(this) }
    }

    @Transactional
    fun createPackageProduct(createModel: PackageProductDetailCreateModel): PackageProductDetailModel {
        ValidatorUtils.validateAndThrow(packageProductDetailCreateModelValidator, createModel)

        val basicProductModel = createModel.basicProductModel
        // 상품코드 채번
        val basicProductCode = with(basicProductModel) {
            basicProductCodeGenerator.getBasicProductCode(partnerId, type, handlingTemperature)
        }
        // 기본상품-모음상품 매핑을 위한 모음상품(BasicProduct) 조회
        val packageProductById = getPackageProductById(createModel)

        // 기본상품-모음상품 매핑 저장
        val packageProducts = createOrUpdatePackageProducts(
            packageProductModels = createModel.packageProductModels,
            packageProductById = packageProductById
        )

        val basicProduct = createModel.toEntity(
            code = basicProductCode,
            packageProductMappings = packageProducts
        )
        return basicProductRepository.save(basicProduct)
            .run { PackageProductDetailModel.fromEntity(this) }
    }

    private fun getPackageProductById(createModel: PackageProductDetailCreateModel) =
        basicProductService.getBasicProducts(createModel.packageProductModels.map { it.packageProduct.id!! })
            .associateBy { it.id }

    private fun createOrUpdatePackageProducts(
        packageProductModels: List<PackageProductCreateModel>,
        entityById: Map<Long?, PackageProductMapping> = emptyMap(),
        packageProductById: Map<Long?, BasicProduct>,
    ): Set<PackageProductMapping> {
        val packageProducts = packageProductModels.map {
            val basicProductPackage = packageProductById[it.packageProduct.id]
                ?: throw BaseRuntimeException(errorCode = ErrorCode.NO_ELEMENT)
            if (it.id == null) it.toEntity(basicProductPackage)
            else {
                val entity = entityById[it.id]
                entity!!.update(it, basicProductPackage)
                entity
            }
        }.run { LinkedHashSet(this) }

        // 연관관계 끊긴 entity 삭제처리
        entityById.values.toSet().minus(packageProducts)
            .forEach(PackageProductMapping::delete)

        return packageProducts
    }
}
