package com.smartfoodnet.fnproduct.product.validator

import com.smartfoodnet.base.*
import com.smartfoodnet.common.error.ValidatorUtils
import com.smartfoodnet.common.error.exception.CreateModelValidateError
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCategoryCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class BasicProductDetailCreateModelValidatorTest {
    @Mock
    lateinit var basicProductRepository: BasicProductRepository

    lateinit var basicProductCreateModelValidator: BasicProductCreateModelValidator
    lateinit var basicProductDetailCreateModelValidator: BasicProductDetailCreateModelValidator
    private var basicproductsSub: List<BasicProduct> = mutableListOf()

    @BeforeEach
    fun init() {
        // 부자재 카테고리 생성
        val subsidiaryMaterialCategories = buildSubsidiaryMaterialCategory()
        // 공통부자재 생성
        basicproductsSub = subsidiaryMaterialCategories.map {
            buildBasicProduct_SUB(name = it.level2Category!!.keyName, subsidiaryMaterialCategory = it)
        }

        basicProductCreateModelValidator = BasicProductCreateModelValidator()
        basicProductDetailCreateModelValidator =
            BasicProductDetailCreateModelValidator(basicProductRepository, basicProductCreateModelValidator)
    }

    @Nested
    @DisplayName("기본상품(type=BASIC) 추가 시,")
    inner class BasicProductBasicType {
        @Test
        @DisplayName("valid input 일 경우, validate 성공한다")
        fun validate_ValidInput_ThenSuccess() {
            // given
            val mockCreateModel = getValidInput()

            // when & then
            ValidatorUtils.validateAndThrow(basicProductDetailCreateModelValidator, mockCreateModel)
        }

        @Nested
        @DisplayName("기본정보 필수 input 이 null/empty 경우,")
        inner class RequiredFieldsBasicType {
            private val separator = "\n\t"

            @Test
            @DisplayName("validate 실패한다 - partnerId, name, barcodeYn, handlingTemperature, basicProductCategory, singlePackagingYn")
            fun checkRequiredFieldsBasicType_InvalidInput_ThenFail() {
                // given
                val mockCreateModel = getInvalidInput_checkRequiredFieldsBasicType()

                val expectedMessage = listOf("CreateModelValidateErrorMessage: ",
                    "화주(고객사) ID 값은 null 이 아닌 값을 입력해주세요.",
                    "상품명 값을 입력해주세요.",
                    "상품바코드기재여부 값을 입력해주세요.",
                    "취급온도 값은 null 이 아닌 값을 입력해주세요.",
                    "상품카테고리 값은 null 이 아닌 값을 입력해주세요.",
                    "단수(포장)여부 값을 입력해주세요.",
                    "유통기한관리여부 값을 입력해주세요.",
                    "박스입수 값은 null 이 아닌 값을 입력해주세요.",
                    "파레트입수 값은 null 이 아닌 값을 입력해주세요.").joinToString(separator, "", separator)

                // when & then
                val ex = assertThrows<CreateModelValidateError> {
                    ValidatorUtils.validateAndThrow(basicProductDetailCreateModelValidator,
                        mockCreateModel)
                }
                assertNotNull(ex.message)
                assertTrue(ex.message!!.contains(expectedMessage))
            }

            @Test
            @DisplayName("validate 실패한다 - 기본상품 카테고리 대분류/중분류")
            fun checkRequiredFieldsBasicType_InvalidBasicProductCategory_ThenFail() {
                // given
                val mockCreateModel = getInvalidBasicProductCategory_checkRequiredFieldsBasicType()

                val expectedMessage = listOf("CreateModelValidateErrorMessage: ",
                    "상품카테고리(대분류) 값을 입력해주세요.",
                    "상품카테고리(중분류) 값을 입력해주세요.").joinToString(separator, "", separator)

                // when & then
                val ex = assertThrows<CreateModelValidateError> {
                    ValidatorUtils.validateAndThrow(basicProductDetailCreateModelValidator,
                        mockCreateModel)
                }
                assertNotNull(ex.message)
                assertTrue(ex.message!!.contains(expectedMessage))
            }
        }

    }

    private fun getValidInput(): BasicProductDetailCreateModel {
        val basicProductModel = buildBasicProductCreateModel(
            type = BasicProductType.BASIC,
            handlingTemperature = HandlingTemperatureType.FREEZE,
        )
        return getInput(basicProductModel)
    }

    private fun getInvalidInput_checkRequiredFieldsBasicType(): BasicProductDetailCreateModel {
        val basicProductModel = buildBasicProductCreateModel(
            partnerId = null,
            name = "",
            barcodeYn = "",
            type = BasicProductType.BASIC,
            handlingTemperature = null,
            basicProductCategory = null,
            singlePackagingYn = "",
            expirationDateManagementYn = "",
            piecesPerBox = null,
            boxesPerPalette = null
        )
        return getInput(basicProductModel)
    }

    private fun getInvalidBasicProductCategory_checkRequiredFieldsBasicType(): BasicProductDetailCreateModel {
        val basicProductModel = buildBasicProductCreateModel(
            type = BasicProductType.BASIC,
            handlingTemperature = HandlingTemperatureType.FREEZE,
            basicProductCategory = BasicProductCategoryCreateModel(level1 = "", level2 = ""),
        )
        return getInput(basicProductModel)
    }

    private fun getInput(basicProductModel: BasicProductCreateModel): BasicProductDetailCreateModel {
        val firstSubBasicProduct = basicproductsSub.first()
        val buildSubsidiaryMaterialCreateModel =
            buildSubsidiaryMaterialCreateModel(subsidiaryMaterial = buildBasicProductSubCreateModel(id = firstSubBasicProduct.id))

        return buildBasicProductDetailCreateModel(basicProductModel = basicProductModel)
            .apply { subsidiaryMaterialModels.add(buildSubsidiaryMaterialCreateModel) }
    }
}
