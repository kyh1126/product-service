package com.smartfoodnet.fnproduct.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.smartfoodnet.base.*
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.code.CodeService
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import com.smartfoodnet.fnproduct.product.model.dto.CategoryDto
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@UTF8AutoConfigureMockMvc
internal class BasicProductControllerTest(
    private val codeService: CodeService,
    private val basicProductCategoryRepository: BasicProductCategoryRepository,
    private val subsidiaryMaterialCategoryRepository: SubsidiaryMaterialCategoryRepository,
    private val objectMapper: ObjectMapper,
    private val mockMvc: MockMvc,
) : AbstractTest() {
    private val basicProductControllerPath = "/basic-products"

    @BeforeAll  // Testcontainers 가 static 으로 떠있기 때문에, DB 저장도 한 번만 실행되어야 한다.
    fun init() {
        codeService.createCodes(
            listOf(
                BasicProductCategoryCodes,
                SubsidiaryMaterialCategoryCodes
            ).flatten()
        )
    }

    @Test
    @DisplayName("특정 화주(고객사) ID 의 기본상품 리스트 조회 api 정상적으로 조회된다")
    fun givenPartnerId_WhenGetBasicProducts_ThenReturn200_Success() {
        // given

        val response = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "id"))
            .run { Page.empty<BasicProduct>(this) }
            .run { PageResponse.of(this) }

        // when & then
        mockMvc.get("$basicProductControllerPath/partners/${partnerId}") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { objectMapper.writeValueAsString(response) }
        }.andDo {
            print()
        }
    }

    @Test
    @DisplayName("기본상품 카테고리 조회 api 정상적으로 조회된다")
    @Transactional
    fun givenBasicProductCategoryId_WhenGetBasicProductCategories_ThenReturn200_Success() {
        // given
        val level1CategoryId = BasicProductCategories.keys.first()

        // 기본 상품 카테고리 생성
        val categories: List<BasicProductCategory> = buildBasicProductCategory()
        val response = categories.groupBy({ it.level1Category },
            { CategoryDto.fromEntity(it.id, it.level2Category) })
            .map { CategoryByLevelModel.fromEntity(it.key, it.value) }

        basicProductCategoryRepository.saveAll(categories)

        // when & then
        mockMvc.get("$basicProductControllerPath/categories") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { objectMapper.writeValueAsString(response) }
            jsonPath("$.payload[0].value") { value(null) }
            jsonPath("$.payload[0].label") {
                val basicProductCategoryLevel1 = BasicProductCategoryCodes.fromId(level1CategoryId)
                value(basicProductCategoryLevel1.keyName)
            }
        }.andDo {
            print()
        }
    }

    @Test
    @DisplayName("부자재 카테고리 조회 api 정상적으로 조회된다")
    @Transactional
    fun givenSubsidiaryMaterialCategoryId_WhenGetSubsidiaryMaterialCategories_ThenReturn200_Success() {
        // given
        val level1CategoryId = SubsidiaryMaterialCategories.keys.first()

        // 부자재 카테고리 생성
        val categories: List<SubsidiaryMaterialCategory> = buildSubsidiaryMaterialCategory()
        val response = categories.groupBy({ it.level1Category },
            { CategoryDto.fromEntity(it.id, it.level2Category) })
            .map { CategoryByLevelModel.fromEntity(it.key, it.value) }

        subsidiaryMaterialCategoryRepository.saveAll(categories)

        // when & then
        mockMvc.get("$basicProductControllerPath/subsidiary-material-categories") {
            param("level1CategoryId", level1CategoryId.toString())
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { objectMapper.writeValueAsString(response) }
            jsonPath("$.payload[0].value") { value(null) }
            jsonPath("$.payload[0].label") {
                val subsidiaryMaterialCategoryLevel1 =
                    SubsidiaryMaterialCategoryCodes.fromId(level1CategoryId)
                value(subsidiaryMaterialCategoryLevel1.keyName)
            }
        }.andDo {
            print()
        }
    }

}
