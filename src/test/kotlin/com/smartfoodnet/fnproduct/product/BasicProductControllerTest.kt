package com.smartfoodnet.fnproduct.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.smartfoodnet.base.*
import com.smartfoodnet.fnproduct.code.CodeRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@UTF8AutoConfigureMockMvc
internal class BasicProductControllerTest(
    private val basicProductCategoryRepository: BasicProductCategoryRepository,
    private val codeRepository: CodeRepository,
    private val objectMapper: ObjectMapper,
    private val mockMvc: MockMvc,
) : AbstractTest() {
    private val basicProductControllerPath = "/basic-products"

    @Test
    @DisplayName("기본상품 카테고리 조회 api 정상적으로 조회된다")
    @Transactional
    fun givenBasicProductCategoryId_WhenGetBasicProductCategories_ThenReturn200_Success() {
        // given
        val level1CategoryId = 1
        val level2CategoryId = BasicProductCategories[level1CategoryId.toLong()]!![0]

        // 기본 상품 카테고리 생성
        val categories: List<BasicProductCategory> = buildBasicProductCategory()
        val response = categories.groupBy({ it.level1Category }, { it.level2Category })
            .map { CategoryByLevelModel.fromEntity(it.key, it.value) }

        codeRepository.saveAll(BasicProductCategoryCodes)
        basicProductCategoryRepository.saveAll(categories)

        // when & then
        mockMvc.get("$basicProductControllerPath/categories") {
            param("level1CategoryId", level1CategoryId.toString())
//            param("level2CategoryId", level2CategoryId.toString())
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { objectMapper.writeValueAsString(response) }
            jsonPath("$.payload[0].value") { value(level1CategoryId) }
            jsonPath("$.payload[0].label") {
                val basicProductCategoryLevel1 = BasicProductCategoryCodes.fromId(level1CategoryId.toLong())
                value(basicProductCategoryLevel1.keyName)
            }
        }.andDo {
            print()
        }
    }

}
