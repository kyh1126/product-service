package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.request.PreShippingProductModel
import com.smartfoodnet.apiclient.response.PostShippingProductModel
import com.smartfoodnet.base.*
import com.smartfoodnet.common.model.response.CommonResponse
import com.smartfoodnet.fnproduct.code.CodeService
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialMapping
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCategoryFinder
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCodeGenerator
import com.smartfoodnet.fnproduct.product.mapper.SubsidiaryMaterialCategoryFinder
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductDetailModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import com.smartfoodnet.fnproduct.warehouse.InWarehouseRepository
import com.smartfoodnet.fnproduct.warehouse.InWarehouseService
import com.smartfoodnet.fnproduct.warehouse.entity.InWarehouse
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.given
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.TestConstructor
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableAutoConfiguration
internal class BasicProductServiceTest(
    private val codeService: CodeService,
    private val inWarehouseService: InWarehouseService,
    private val basicProductService: BasicProductService,
    private val basicProductCategoryFinder: BasicProductCategoryFinder,
    private val subsidiaryMaterialCategoryFinder: SubsidiaryMaterialCategoryFinder,
    private val basicProductCodeGenerator: BasicProductCodeGenerator,
) : AbstractTest() {
    @MockBean
    lateinit var basicProductRepository: BasicProductRepository

    @MockBean
    lateinit var inWarehouseRepository: InWarehouseRepository

    @MockBean
    lateinit var basicProductCategoryRepository: BasicProductCategoryRepository

    @MockBean
    lateinit var subsidiaryMaterialCategoryRepository: SubsidiaryMaterialCategoryRepository

    @MockBean
    lateinit var wmsApiClient: WmsApiClient

    lateinit var warehouse: InWarehouse
    private var basicProductCategories: List<BasicProductCategory> = mutableListOf()
    private var subsidiaryMaterialCategories: List<SubsidiaryMaterialCategory> = mutableListOf()
    private var basicproductsSub: List<BasicProduct> = mutableListOf()

    @BeforeAll  // Testcontainers 가 static 으로 떠있기 때문에, DB 저장도 한 번만 실행되어야 한다. + 필요한 데이터 mocking
    fun init() {
        // 코드 생성
        codeService.createCodes(
            listOf(
                BasicProductCategoryCodes,
                SubsidiaryMaterialCategoryCodes
            ).flatten()
        )

        // 입고처 생성
        warehouse = buildWarehouse(partnerId)
        // 기본 상품 카테고리 생성
        basicProductCategories = buildBasicProductCategory()
        // 부자재 카테고리 생성
        subsidiaryMaterialCategories = buildSubsidiaryMaterialCategory()
        // 공통부자재 생성
        basicproductsSub = subsidiaryMaterialCategories.map {
            buildBasicProduct_SUB(
                partnerId = partnerId,
                name = it.level2Category!!.keyName,
                subsidiaryMaterialCategory = it
            )
        }
    }

    @BeforeEach
    fun initEach() {
        given(inWarehouseRepository.findById(anyLong())).willReturn(Optional.of(warehouse))
        given(basicProductCategoryRepository.findById(anyLong()))
            .willReturn(Optional.of(basicProductCategories.first()))
        given(
            basicProductCategoryRepository.findByLevel1CategoryAndLevel2Category(
                anyString(),
                anyString()
            )
        ).willReturn(listOf(basicProductCategories.first()))
        given(subsidiaryMaterialCategoryRepository.findById(anyLong()))
            .willReturn(Optional.of(subsidiaryMaterialCategories.first()))
        given(
            subsidiaryMaterialCategoryRepository.findByLevel1CategoryAndLevel2Category(
                anyString(),
                anyString()
            )
        ).willReturn(listOf(subsidiaryMaterialCategories.first()))
        basicproductsSub.forEach {
            given(basicProductRepository.findById(it.id))
                .willReturn(Optional.of(it))
        }
    }

    @Nested
    @DisplayName("기본상품(type=BASIC)")
    inner class BasicProductBasicType {
        private var basicProductCode: String? = null
        private var basicProductId: Long = 0

        @Test
        @DisplayName("추가 성공한다")
        fun createBasicProduct_ValidInput_ThenSuccess() {
            // given
            val firstBasicProductSub = basicproductsSub.first()
            given(basicProductRepository.findAllById(listOf(firstBasicProductSub.id)))
                .willReturn(listOf(firstBasicProductSub))

            val buildSubsidiaryMaterialMappingCreateModel =
                buildSubsidiaryMaterialMappingCreateModel(subsidiaryMaterialId = firstBasicProductSub.id)

            val mockCreateModel = buildBasicProductDetailCreateModel(
                basicProductModel = buildBasicProductCreateModel(
                    type = BasicProductType.BASIC,
                    partnerId = partnerId,
                    partnerCode = partnerCode,
                    handlingTemperature = HandlingTemperatureType.FREEZE,
                )
            ).apply { subsidiaryMaterialMappingModels.add(buildSubsidiaryMaterialMappingCreateModel) }

            // 저장 가능한 Entity 로 변환
            val basicProductCreateModel = mockCreateModel.basicProductModel
            basicProductCode = with(basicProductCreateModel) {
                basicProductCodeGenerator.getBasicProductCode(
                    partnerId!!,
                    partnerCode!!,
                    type,
                    handlingTemperature!!
                )
            }
            // BasicProductCategory: 있는거 조회해서 넘겨야함
            val basicProductCategory = getBasicProductCategory(basicProductCreateModel)
            // SubsidiaryMaterialCategory: 있는거 조회해서 넘겨야함
            val subsidiaryMaterialCategory = getSubsidiaryMaterialCategory(basicProductCreateModel)
            // Warehouse: 있는거 조회해서 넘겨야함
            val warehouse = getWarehouse(basicProductCreateModel)
            // subsidiaryMaterial: (BasicProduct) 조회해서 넘겨야함
            val subsidiaryMaterialById = getSubsidiaryMaterialById(mockCreateModel)

            // 기본상품-부자재 매핑 저장
            val subsidiaryMaterialMappings: Set<SubsidiaryMaterialMapping>? =
                ReflectionTestUtils.invokeMethod(
                    basicProductService,
                    "createOrUpdateSubsidiaryMaterialMappings",
                    mockCreateModel.subsidiaryMaterialMappingModels,
                    emptyMap<Long?, SubsidiaryMaterialMapping>(),
                    subsidiaryMaterialById
                )

            val mockBasicProduct = getBasicProduct(
                basicProductCode!!,
                mockCreateModel,
                basicProductCategory,
                subsidiaryMaterialCategory,
                subsidiaryMaterialMappings,
                warehouse
            )
            basicProductId = mockBasicProduct.id

            val postShippingProductModel = PostShippingProductModel(
                shippingProductId = 1L,
                productCode = basicProductCode!!,
                salesProductId = 1L,
                salesProductCode = basicProductCode
            )
            given(
                wmsApiClient.createShippingProduct(
                    PreShippingProductModel.fromEntity(mockBasicProduct)
                )
            ).willReturn(CommonResponse(postShippingProductModel))
            given(basicProductRepository.save(any())).willReturn(mockBasicProduct)
            given(basicProductRepository.findById(basicProductId))
                .willReturn(Optional.of(mockBasicProduct))
            given(basicProductRepository.findAllById(listOf(basicProductId)))
                .willReturn(listOf(mockBasicProduct))

            // when
            val actualBasicProductDetailModel =
                basicProductService.createBasicProductWithNosnos(mockCreateModel)

            // then
            val expected = BasicProductDetailModel.fromEntity(mockBasicProduct, subsidiaryMaterialById)

            assertNotNull(BasicProductDetailModel)
            verify(basicProductRepository, times(1)).save(any())
            assertEquals(
                expected.basicProductModel,
                actualBasicProductDetailModel.basicProductModel
            )
        }

        @Test
        @DisplayName("수정 성공한다")
        fun updateBasicProduct_ValidInput_ThenSuccess() {
            // given
            // 부자재 수정
            val secondBasicProductSub = basicproductsSub[1]
            given(basicProductRepository.findAllById(listOf(secondBasicProductSub.id)))
                .willReturn(listOf(secondBasicProductSub))

            if (basicProductCode == null) {
                createBasicProduct_ValidInput_ThenSuccess()
            }

            val buildSubsidiaryMaterialMappingCreateModel =
                buildSubsidiaryMaterialMappingCreateModel(subsidiaryMaterialId = secondBasicProductSub.id)

            val mockUpdateModel = buildBasicProductDetailCreateModel(
                basicProductModel = buildBasicProductCreateModel(
                    type = BasicProductType.BASIC,
                    partnerId = partnerId,
                    partnerCode = partnerCode,
                    handlingTemperature = HandlingTemperatureType.FREEZE,
                )
            ).apply { subsidiaryMaterialMappingModels.add(buildSubsidiaryMaterialMappingCreateModel) }

            // 저장 가능한 Entity 로 변환
            val basicProductCreateModel = mockUpdateModel.basicProductModel

            val basicProductCategory = getBasicProductCategory(basicProductCreateModel)
            val subsidiaryMaterialCategory = getSubsidiaryMaterialCategory(basicProductCreateModel)
            val warehouse = getWarehouse(basicProductCreateModel)
            val subsidiaryMaterialById = getSubsidiaryMaterialById(mockUpdateModel)

            val basicProduct = basicProductService.getBasicProducts(listOf(basicProductId)).first()

            // 기본상품-부자재 매핑 저장
            val entityById = basicProduct.subsidiaryMaterialMappings.associateBy { it.id }
            val subsidiaryMaterialMappings: Set<SubsidiaryMaterialMapping>? =
                ReflectionTestUtils.invokeMethod(
                    basicProductService,
                    "createOrUpdateSubsidiaryMaterialMappings",
                    mockUpdateModel.subsidiaryMaterialMappingModels,
                    entityById,
                    subsidiaryMaterialById
                )

            val mockBasicProduct = getBasicProduct(
                basicProductCode!!,
                mockUpdateModel,
                basicProductCategory,
                subsidiaryMaterialCategory,
                subsidiaryMaterialMappings,
                warehouse
            )

            // when
            val actualBasicProductDetailModel =
                basicProductService.updateBasicProduct(mockBasicProduct.id, mockUpdateModel)

            // then
            val expected = BasicProductDetailModel.fromEntity(mockBasicProduct, subsidiaryMaterialById)

            assertNotNull(BasicProductDetailModel)
            assertEquals(
                expected.basicProductModel,
                actualBasicProductDetailModel.basicProductModel
            )
        }
    }

    private fun getBasicProduct(
        basicProductCode: String,
        mockUpdateModel: BasicProductDetailCreateModel,
        basicProductCategory: BasicProductCategory?,
        subsidiaryMaterialCategory: SubsidiaryMaterialCategory?,
        subsidiaryMaterialMappings: Set<SubsidiaryMaterialMapping>?,
        warehouse: InWarehouse
    ): BasicProduct {
        return mockUpdateModel.toEntity(
            code = basicProductCode,
            basicProductCategory = basicProductCategory,
            subsidiaryMaterialCategory = subsidiaryMaterialCategory,
            subsidiaryMaterialMappings = subsidiaryMaterialMappings!!,
            inWarehouse = warehouse
        ).apply {
            this.shippingProductId = 1L
            this.salesProductId = 1L
            this.productCode = basicProductCode
            this.salesProductCode = basicProductCode
        }
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
        basicProductService.getBasicProducts(createModel.subsidiaryMaterialMappingModels.map { it.subsidiaryMaterialId!! })
            .associateBy { it.id }
}
