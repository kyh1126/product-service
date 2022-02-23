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
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
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
        val packageProduct = getPackageProductByProductId(productId)

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
            basicProductCodeGenerator.getBasicProductCode(
                partnerId!!,
                partnerCode!!,
                BasicProductType.PACKAGE,
                HandlingTemperatureType.MIX
            )
        }

        val packageProductMappingModels = createModel.packageProductMappingModels
        // 모음상품-기본상품 매핑을 위한 기본상품(BasicProduct) 조회
        val basicProductById =
            basicProductService.getBasicProducts(packageProductMappingModels.map { it.basicProductId })
                .associateBy { it.id }

        // 모음상품-기본상품 매핑 저장
        val packageProductMappings = createPackageProductMappings(
            packageProductMappingModels = packageProductMappingModels,
            basicProductById = basicProductById
        )

        val basicProduct = createModel.toEntity(
            code = basicProductCode!!,
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

        val packageProduct = getPackageProductByProductId(productId)
        with(updateModel.packageProductModel) {
            packageProduct.update(name!!, activeYn)
        }

        val basicProductById =
            packageProduct.packageProductMappings.map { it.selectedBasicProduct }
                .associateBy { it.id }
        return toPackageProductDetailModel(packageProduct, basicProductById)
    }

    private fun getPackageProductByProductId(productId: Long) =
        basicProductService.getBasicProducts(listOf(productId))
            .first { it.type == BasicProductType.PACKAGE }

    private fun createPackageProductMappings(
        packageProductMappingModels: List<PackageProductMappingCreateModel>,
        basicProductById: Map<Long?, BasicProduct>,
    ): Set<PackageProductMapping> {
        return packageProductMappingModels.map {
            val selectedBasicProduct = basicProductById[it.basicProductId]
                ?: throw BaseRuntimeException(errorCode = ErrorCode.NO_ELEMENT)
            it.toEntity(selectedBasicProduct)
        }.run { LinkedHashSet(this) }
    }

    private fun toPackageProductDetailModel(
        packageProduct: BasicProduct,
        basicProductById: Map<Long?, BasicProduct>,
    ) = PackageProductDetailModel.fromEntity(packageProduct, basicProductById)

}
