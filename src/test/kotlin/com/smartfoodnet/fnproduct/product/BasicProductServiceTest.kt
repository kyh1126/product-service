package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.base.*
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.error.exception.ErrorCode
import com.smartfoodnet.fnproduct.code.CodeService
import com.smartfoodnet.fnproduct.product.entity.*
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCategoryFinder
import com.smartfoodnet.fnproduct.product.mapper.BasicProductCodeGenerator
import com.smartfoodnet.fnproduct.product.mapper.SubsidiaryMaterialCategoryFinder
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialCreateModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductDetailModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.given
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import java.util.*
import kotlin.random.Random

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

        // 화주(고객사) 생성
        partner = buildPartner()
        // 입고처 생성
        warehouse = buildWarehouse(partner)
        // 기본 상품 카테고리 생성
        basicProductCategories = buildBasicProductCategory()
        // 부자재 카테고리 생성
        subsidiaryMaterialCategories = buildSubsidiaryMaterialCategory()
        // 공통부자재 생성
        basicproductsSub = subsidiaryMaterialCategories.map {
            buildBasicProduct_SUB(
                partnerId = partner.id!!,
                name = it.level2Category!!.keyName,
                subsidiaryMaterialCategory = it
            )
        }
    }

    @BeforeEach
    fun initEach() {
        given(partnerRepository.findById(anyLong())).willReturn(Optional.of(partner))
        given(warehouseRepository.findById(anyLong())).willReturn(Optional.of(warehouse))
        given(basicProductCategoryRepository.findById(anyLong()))
            .willReturn(Optional.of(basicProductCategories.first()))
        given(
            basicProductCategoryRepository.findByLevel1CategoryAndLevel2Category(
                anyLong(),
                anyLong()
            )
        ).willReturn(listOf(basicProductCategories.first()))
        given(subsidiaryMaterialCategoryRepository.findById(anyLong()))
            .willReturn(Optional.of(subsidiaryMaterialCategories.first()))
        given(
            subsidiaryMaterialCategoryRepository.findByLevel1CategoryAndLevel2Category(
                anyLong(),
                anyLong()
            )
        ).willReturn(listOf(subsidiaryMaterialCategories.first()))
        basicproductsSub.forEach {
            given(basicProductRepository.findById(it.id!!))
                .willReturn(Optional.of(it))
        }
    }

    @Nested
    @DisplayName("기본상품(type=BASIC)")
    inner class BasicProductBasicType {
        private var productId = Random.nextLong(0, Long.MAX_VALUE)
        private var basicProductCode: String? = null

        @Test
        @DisplayName("추가 성공한다")
        fun createBasicProduct_ValidInput_ThenSuccess() {
            // given
            val firstSubBasicProduct = basicproductsSub.first()
            given(basicProductRepository.findAllById(listOf(firstSubBasicProduct.id)))
                .willReturn(listOf(firstSubBasicProduct))

            val buildSubsidiaryMaterialCreateModel =
                buildSubsidiaryMaterialCreateModel(
                    subsidiaryMaterial = buildBasicProductSubCreateModel(
                        id = firstSubBasicProduct.id
                    )
                )

            val mockCreateModel = buildBasicProductDetailCreateModel(
                basicProductModel = buildBasicProductCreateModel(
                    type = BasicProductType.BASIC,
                    partnerId = partner.id,
                    handlingTemperature = HandlingTemperatureType.FREEZE,
                )
            ).apply { subsidiaryMaterialModels.add(buildSubsidiaryMaterialCreateModel) }

            // 저장 가능한 Entity 로 변환
            val basicProductCreateModel = mockCreateModel.basicProductModel
            basicProductCode = with(basicProductCreateModel) {
                basicProductCodeGenerator.getBasicProductCode(partnerId, type, handlingTemperature)
            }
            // BasicProductCategory: 있는거 조회해서 넘겨야함
            val basicProductCategory = getBasicProductCategory(basicProductCreateModel)
            // SubsidiaryMaterialCategory: 있는거 조회해서 넘겨야함
            val subsidiaryMaterialCategory = getSubsidiaryMaterialCategory(basicProductCreateModel)
            // Warehouse: 있는거 조회해서 넘겨야함
            val warehouse = getWarehouse(basicProductCreateModel)
            // subsidiaryMaterial: (BasicProduct) 조회해서 넘겨야함
            val subsidiaryMaterialById = getSubsidiaryMaterialById(mockCreateModel)

            // ExpirationDateInfo 저장
            val expirationDateInfo = createOrUpdateExpirationDateInfo(basicProductCreateModel)
            // 기본상품-부자재 매핑 저장
            val subsidiaryMaterials = createOrUpdateSubsidiaryMaterials(
                subsidiaryMaterialModels = mockCreateModel.subsidiaryMaterialModels,
                subsidiaryMaterialById = subsidiaryMaterialById
            )

            val mockBasicProduct = mockCreateModel.toEntity(
                code = basicProductCode,
                basicProductCategory = basicProductCategory,
                subsidiaryMaterialCategory = subsidiaryMaterialCategory,
                expirationDateInfo = expirationDateInfo,
                subsidiaryMaterials = subsidiaryMaterials,
                warehouse = warehouse
            ).apply { id = productId }
            given(basicProductRepository.save(any())).willReturn(mockBasicProduct)
            given(basicProductRepository.findById(productId))
                .willReturn(Optional.of(mockBasicProduct))

            // when
            val actualBasicProductDetailModel =
                basicProductService.createBasicProduct(mockCreateModel)

            // then
            assertNotNull(BasicProductDetailModel)
            verify(basicProductRepository, times(1)).save(any())
            assertEquals(
                BasicProductDetailModel.fromEntity(mockBasicProduct, subsidiaryMaterialById),
                actualBasicProductDetailModel
            )
        }

        @Test
        @DisplayName("수정 성공한다")
        fun updateBasicProduct_ValidInput_ThenSuccess() {
            // given
            if (basicProductCode == null) {
                createBasicProduct_ValidInput_ThenSuccess()
            }

            // 부자재 수정
            val secondSubBasicProduct = basicproductsSub[1]
            given(basicProductRepository.findAllById(listOf(secondSubBasicProduct.id)))
                .willReturn(listOf(secondSubBasicProduct))

            val buildSubsidiaryMaterialCreateModel =
                buildSubsidiaryMaterialCreateModel(
                    subsidiaryMaterial = buildBasicProductSubCreateModel(
                        id = secondSubBasicProduct.id
                    )
                )

            val mockUpdateModel = buildBasicProductDetailCreateModel(
                basicProductModel = buildBasicProductCreateModel(
                    type = BasicProductType.BASIC,
                    partnerId = partner.id,
                    handlingTemperature = HandlingTemperatureType.FREEZE,
                )
            ).apply { subsidiaryMaterialModels.add(buildSubsidiaryMaterialCreateModel) }

            // 저장 가능한 Entity 로 변환
            val basicProductCreateModel = mockUpdateModel.basicProductModel

            val basicProductCategory = getBasicProductCategory(basicProductCreateModel)
            val subsidiaryMaterialCategory = getSubsidiaryMaterialCategory(basicProductCreateModel)
            val warehouse = getWarehouse(basicProductCreateModel)
            val subsidiaryMaterialById = getSubsidiaryMaterialById(mockUpdateModel)

            // ExpirationDateInfo 저장
            val expirationDateInfo = createOrUpdateExpirationDateInfo(basicProductCreateModel)
            // 기본상품-부자재 매핑 저장
            val subsidiaryMaterials = createOrUpdateSubsidiaryMaterials(
                subsidiaryMaterialModels = mockUpdateModel.subsidiaryMaterialModels,
                subsidiaryMaterialById = subsidiaryMaterialById
            )

            val mockBasicProduct = mockUpdateModel.toEntity(
                code = basicProductCode,
                basicProductCategory = basicProductCategory,
                subsidiaryMaterialCategory = subsidiaryMaterialCategory,
                expirationDateInfo = expirationDateInfo,
                subsidiaryMaterials = subsidiaryMaterials,
                warehouse = warehouse
            ).apply { id = productId }

            // when
            val actualBasicProductDetailModel =
                basicProductService.updateBasicProduct(productId, mockUpdateModel)

            // then
            assertNotNull(BasicProductDetailModel)
            assertEquals(
                BasicProductDetailModel.fromEntity(mockBasicProduct, subsidiaryMaterialById),
                actualBasicProductDetailModel
            )
        }
    }

    private fun getBasicProductCategory(basicProductCreateModel: BasicProductCreateModel): BasicProductCategory? {
        return (basicProductCreateModel.basicProductCategory)?.let {
            basicProductCategoryFinder.getBasicProductCategoryByKeyName(it.level1!!, it.level2!!)
        }
    }

    private fun getSubsidiaryMaterialCategory(basicProductCreateModel: BasicProductCreateModel): SubsidiaryMaterialCategory? {
        return (basicProductCreateModel.subsidiaryMaterialCategory)?.let {
            subsidiaryMaterialCategoryFinder.getSubsidiaryMaterialCategoryByKeyName(
                it.level1!!,
                it.level2!!
            )
        }
    }

    private fun getWarehouse(basicProductCreateModel: BasicProductCreateModel) =
        warehouseService.getWarehouse(basicProductCreateModel.warehouse.id!!)

    private fun getSubsidiaryMaterialById(createModel: BasicProductDetailCreateModel) =
        basicProductService.getBasicProducts(createModel.subsidiaryMaterialModels.map { it.subsidiaryMaterial.id!! })
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
    ): Set<SubsidiaryMaterial> {
        val subsidiaryMaterials = subsidiaryMaterialModels.map {
            val basicProductSub = subsidiaryMaterialById[it.subsidiaryMaterial.id]
                ?: throw BaseRuntimeException(errorCode = ErrorCode.NO_ELEMENT)
            if (it.id == null) it.toEntity(basicProductSub)
            else {
                val entity = entityById[it.id]
                entity!!.update(it, basicProductSub)
                entity
            }
        }.run { LinkedHashSet(this) }

        // 연관관계 끊긴 entity 삭제처리
        entityById.values.toSet().minus(subsidiaryMaterials)
            .forEach(SubsidiaryMaterial::delete)

        return subsidiaryMaterials
    }
}
