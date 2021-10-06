package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.error.ValidatorUtils
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.partner.PartnerService
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
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
    private val partnerService: PartnerService,
    private val basicProductRepository: BasicProductRepository,
    private val basicProductCategoryRepository: BasicProductCategoryRepository,
    private val subsidiaryMaterialCategoryRepository: SubsidiaryMaterialCategoryRepository,
    private val basicProductDetailCreateModelValidator: BasicProductDetailCreateModelValidator,
) {

    fun getBasicProducts(partnerId: Long, type: BasicProductType?, page: Pageable): PageResponse<BasicProductModel> {
        return basicProductRepository.findByPartnerIdAndType(partnerId, type, page)
            .map(BasicProductModel::fromEntity)
            .run { PageResponse.of(this) }
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

    @Transactional
    fun createBasicProduct(createModel: BasicProductDetailCreateModel): BasicProductDetailModel {
        ValidatorUtils.validateAndThrow(basicProductDetailCreateModelValidator, createModel)

        val partnerId = createModel.basicProductModel.partnerId
        val typeCode = createModel.basicProductModel.type.code
        val temperatureCode = createModel.basicProductModel.handlingTemperature?.code

        /**
         * 상품코드 채번 규칙<p>
         *
         * @see <a href="https://docs.google.com/spreadsheets/d/1z1vAZCbcleMM0v5nDbJl-aER0ExCgMx_50-obAxsd0c/edit#gid=1591663723">상품코드채번규칙</a>
         */
        val code: String = getBasicProductCode(partnerId, typeCode, temperatureCode)
//        basicProductCategory: BasicProductCategory?,
//        subsidiaryMaterialCategory: SubsidiaryMaterialCategory?,
//        subsidiaryMaterial: SubsidiaryMaterial,
//        warehouse: Warehouse,
        // TODO: BasicProduct 생성 로직 작성중..
        val basicProduct = BasicProduct(type = BasicProductType.BASIC)//createModel.toEntity(code)
        basicProductRepository.save(basicProduct)

        return toBasicProductDetailModel(basicProduct)
    }

    private fun getBasicProductCode(
        partnerId: Long?,
        typeCode: String?,
        temperatureCode: String?,
    ): String {
        val customerNumber = partnerId
            ?.let { partnerService.getPartner(it) }?.customerNumber
        val basicProductCodeTypes = setOf(BasicProductType.BASIC, BasicProductType.PACKAGE)

        // 00001 부터 시작
        val totalProductCount = basicProductRepository.countByPartnerIdAndTypeIn(partnerId, basicProductCodeTypes)
            .run { String.format("%05d", this + 1) }

        return customerNumber + typeCode + temperatureCode + totalProductCount
    }

    private fun toBasicProductDetailModel(basicProduct: BasicProduct): BasicProductDetailModel {
        return BasicProductDetailModel.fromEntity(basicProduct)
    }

}
