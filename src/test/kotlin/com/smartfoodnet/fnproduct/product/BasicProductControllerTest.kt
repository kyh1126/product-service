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
        val level1KeyName = "농산"
        val categories = getBasicProductCategories(level1KeyName)
        val response = categories.groupBy({ it.level1Category }, { it.level2Category })
            .map { CategoryByLevelModel.fromEntity(it.key, it.value) }

        categories.flatMap { listOf(it.level1Category, it.level2Category) }
            .also { codeRepository.saveAll(it) }
        basicProductCategoryRepository.saveAll(categories)

        // when & then
        mockMvc.get("$basicProductControllerPath/categories") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { objectMapper.writeValueAsString(response) }
            jsonPath("$.payload[0].value") { value("1") }
            jsonPath("$.payload[0].label") { value(level1KeyName) }
        }.andDo {
            print()
        }
    }

    fun getBasicProductCategories(keyName: String = "농산"): List<BasicProductCategory> {
        if (keyName !in BasicProductCategories) {
            throw NoSuchElementException("${BasicProductCategories.keys} 중 입력해주세요")
        }
        val level1Category = buildBasicProductCategoryLevel1(keyName = keyName)
        val level2CategoryNames: List<String> = BasicProductCategories[keyName]!!

        return (1..level2CategoryNames.size).map {
            BasicProductCategory(
                id = it.toLong(),
                level1Category = level1Category,
                level2Category = buildBasicProductCategoryLevel2(
                    keyId = it,
                    keyName = level2CategoryNames[it - 1]
                )
            )
        }
    }

}
