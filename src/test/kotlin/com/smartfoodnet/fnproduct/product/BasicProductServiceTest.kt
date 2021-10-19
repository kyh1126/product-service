package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.base.*
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.error.exception.ErrorCode
import com.smartfoodnet.fnproduct.code.CodeService
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.Partner
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import com.smartfoodnet.fnproduct.product.entity.Warehouse
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCategoryFinder
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCodeGenerator
import com.smartfoodnet.fnproduct.product.mapper.SubsidiaryMaterialCategoryFinder
import com.smartfoodnet.fnproduct.product.model.response.BasicProductDetailModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.given
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.transaction.annotation.Transactional
import java.util.*

@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableAutoConfiguration
internal class BasicProductServiceTest(
    private val codeService: CodeService,
    private val warehouseService: WarehouseService,
    private val basicProductService: BasicProductService,
    private val basicProductCategoryFinder: BasicProductCategoryFinder,
    private val subsidiaryMaterialCategoryFinder: SubsidiaryMaterialCategoryFinder,
    private val basicProductCodeGenerator: BasicProductCodeGenerator,
) : AbstractTest() {
    @MockBean
    lateinit var basicProductRepository: BasicProductRepository

    @MockBean
    lateinit var partnerRepository: PartnerRepository

    @MockBean
    lateinit var warehouseRepository: WarehouseRepository

    @MockBean
    lateinit var basicProductCategoryRepository: BasicProductCategoryRepository

    @MockBean
    lateinit var subsidiaryMaterialCategoryRepository: SubsidiaryMaterialCategoryRepository

    lateinit var partner: Partner
    lateinit var warehouse: Warehouse
    private var subsidiaryMaterialCategories: List<SubsidiaryMaterialCategory> = mutableListOf()
    private var basicproductsSub: List<BasicProduct> = mutableListOf()

    @BeforeAll  // Testcontainers 가 static 으로 떠있기 때문에, DB 저장도 한 번만 실행되어야 한다. + 필요한 데이터 mocking
    fun init() {
        // 코드 생성
        codeService.createCodes(listOf(BasicProductCategoryCodes, SubsidiaryMaterialCategoryCodes).flatten())

        // 화주(고객사) 생성
        partner = buildPartner()
        given(partnerRepository.findById(anyLong())).willReturn(Optional.of(partner))
        // 입고처 생성
        warehouse = buildWarehouse(partner)
        given(warehouseRepository.findById(anyLong())).willReturn(Optional.of(warehouse))
        // 기본 상품 카테고리 생성
        given(basicProductCategoryRepository.findById(anyLong())).willReturn(Optional.of(buildBasicProductCategory().first()))
        given(basicProductCategoryRepository.findByLevel1CategoryAndLevel2Category(anyLong(), anyLong()))
            .willReturn(listOf(buildBasicProductCategory().first()))
        // 부자재 카테고리 생성
        subsidiaryMaterialCategories = buildSubsidiaryMaterialCategory()
        given(subsidiaryMaterialCategoryRepository.findById(anyLong()))
            .willReturn(Optional.of(subsidiaryMaterialCategories.first()))
        given(subsidiaryMaterialCategoryRepository.findByLevel1CategoryAndLevel2Category(anyLong(), anyLong()))
            .willReturn(listOf(subsidiaryMaterialCategories.first()))
        // 공통부자재 생성
        basicproductsSub = subsidiaryMaterialCategories.map {
            buildBasicProduct_SUB(
                partnerId = partner.id!!,
                name = it.level2Category!!.keyName,
                subsidiaryMaterialCategory = it
            )
        }
        basicproductsSub.forEach { given(basicProductRepository.findById(it.id!!)).willReturn(Optional.of(it)) }
    }

    @Test
    @DisplayName("기본상품(type=BASIC) 추가 성공한다")
    @Transactional
    fun createBasicProduct_BASIC_ValidInput_ThenSuccess() {
        // given
        val firstSubBasicProduct = basicproductsSub.first()
        given(basicProductRepository.findAllById(listOf(firstSubBasicProduct.id)))
            .willReturn(listOf(firstSubBasicProduct))

        val buildSubsidiaryMaterialCreateModel =
            buildSubsidiaryMaterialCreateModel(subsidiaryMaterial = buildBasicProductSubCreateModel(id = firstSubBasicProduct.id))

        val mockCreateModel = buildBasicProductDetailCreateModel(
            basicProductModel = buildBasicProductCreateModel(
                type = BasicProductType.BASIC,
                partnerId = partner.id,
                handlingTemperature = HandlingTemperatureType.FREEZE,
            )
        ).apply { subsidiaryMaterialModels.add(buildSubsidiaryMaterialCreateModel) }

        // 저장 가능한 Entity 로 변환
        val basicProductCreateModel = mockCreateModel.basicProductModel
        val basicProductCode = with(basicProductCreateModel) {
            basicProductCodeGenerator.getBasicProductCode(partnerId!!, type, handlingTemperature?.code)
        }
        // BasicProductCategory: 있는거 조회해서 넘겨야함
        val basicProductCategory = (basicProductCreateModel.basicProductCategory)?.let {
            basicProductCategoryFinder.getBasicProductCategoryByKeyName(it.level1!!, it.level2!!)
        }
        // SubsidiaryMaterialCategory: 있는거 조회해서 넘겨야함
        val subsidiaryMaterialCategory = (basicProductCreateModel.subsidiaryMaterialCategory)?.let {
            subsidiaryMaterialCategoryFinder.getSubsidiaryMaterialCategoryByKeyName(it.level1!!, it.level2!!)
        }
        // Warehouse: 있는거 조회해서 넘겨야함
        val warehouse = warehouseService.getWarehouse(basicProductCreateModel.warehouse.id!!)
        // subsidiaryMaterial: (BasicProduct) 조회해서 넘겨야함
        val subsidiaryMaterialById =
            basicProductService.getBasicProducts(mockCreateModel.subsidiaryMaterialModels.map { it.subsidiaryMaterial.id!! })
                .associateBy { it.id }

        // ExpirationDateInfo 저장
        val expirationDateInfo = basicProductCreateModel.expirationDateInfoModel?.toEntity()
        // 기본상품-부자재 매핑 저장
        val subsidiaryMaterials = mockCreateModel.subsidiaryMaterialModels.map {
            val basicProductSub = subsidiaryMaterialById[it.subsidiaryMaterial.id]
                ?: throw BaseRuntimeException(errorCode = ErrorCode.NO_ELEMENT)
            it.toEntity(basicProductSub)
        }.toMutableList()

        val mockBasicProduct = mockCreateModel.toEntity(
            code = basicProductCode,
            basicProductCategory = basicProductCategory,
            subsidiaryMaterialCategory = subsidiaryMaterialCategory,
            expirationDateInfo = expirationDateInfo,
            subsidiaryMaterials = subsidiaryMaterials,
            warehouse = warehouse
        )
        given(basicProductRepository.save(any())).willReturn(mockBasicProduct)
        given(basicProductRepository.findById(anyLong())).willReturn(Optional.of(mockBasicProduct))

        // when
        val actualBasicProductDetailModel = basicProductService.createBasicProduct(mockCreateModel)

        // then
        assertNotNull(BasicProductDetailModel)
        verify(basicProductRepository, times(1)).save(any())
        assertEquals(BasicProductDetailModel.fromEntity(mockBasicProduct, subsidiaryMaterialById),
            actualBasicProductDetailModel)
    }

}
