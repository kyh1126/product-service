package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.base.*
import com.smartfoodnet.fnproduct.code.CodeRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.entity.Partner
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.validator.BasicProductDetailCreateModelValidator
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@SpringBootTest(classes = [BasicProductService::class])
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableAutoConfiguration
internal class BasicProductServiceTest(
    private val basicProductService: BasicProductService,
    private val codeRepository: CodeRepository,
) : AbstractTest() {
    @MockBean
    lateinit var partnerRepository: PartnerRepository

    @MockBean
    lateinit var basicProductRepository: BasicProductRepository

    @MockBean
    lateinit var basicProductCategoryRepository: BasicProductCategoryRepository

    @MockBean
    lateinit var subsidiaryMaterialRepository: SubsidiaryMaterialRepository

    @MockBean
    lateinit var subsidiaryMaterialCategoryRepository: SubsidiaryMaterialCategoryRepository

    @MockBean
    lateinit var basicProductDetailCreateModelValidator: BasicProductDetailCreateModelValidator

    lateinit var partner: Partner
    private var basicProductCategories: List<BasicProductCategory> = mutableListOf()
    private var subsidiaryMaterialCategories: List<SubsidiaryMaterialCategory> = mutableListOf()
    private var basicProductSub: List<BasicProduct> = mutableListOf()

    @BeforeAll  // Testcontainers 가 static 으로 떠있기 때문에, DB 저장도 한 번만 실행되어야 한다.
    fun given() {
        // 코드 생성
        codeRepository.saveAll(listOf(BasicProductCategoryCodes, SubsidiaryMaterialCategoryCodes).flatten())
        // 화주(고객사) 생성
        partner = partnerRepository.save(buildPartner())
        // 기본 상품 카테고리 생성
        basicProductCategories = basicProductCategoryRepository.saveAll(buildBasicProductCategory())
        // 부자재 카테고리 생성
        subsidiaryMaterialCategories = subsidiaryMaterialCategoryRepository.saveAll(buildSubsidiaryMaterialCategory())
        // 공통부자재 생성
        basicProductSub = subsidiaryMaterialCategories.map {
            buildBasicProduct_SUB(
                partnerId = partner.id!!,
                name = it.level2Category!!.keyName,
                subsidiaryMaterialCategory = it
            )
        }
        basicProductRepository.saveAll(basicProductSub)
    }

}
