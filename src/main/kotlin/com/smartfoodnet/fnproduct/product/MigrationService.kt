package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.apiclient.PartnerApiClient
import com.smartfoodnet.fnproduct.product.mapper.BasicProductExcelModelMapper
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialSearchCondition
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sfn.excel.module.workbook.read.ExcelReadUtils

@Service
@Transactional(readOnly = true)
class MigrationService(
    @Value("\${spring.data.web.pageable.max-page-size:50}")
    private val maxPageSize: Int,
    private val excelMapper: BasicProductExcelModelMapper,
    private val basicProductService: BasicProductService,
    private val partnerApiClient: PartnerApiClient,
) {
    private val defaultSubsidiaryMaterialName = "없음"

    @Transactional
    fun excelToBasicProduct(fileName: String?, file: MultipartFile?) {
        if (file == null) throw IllegalArgumentException("엑셀 파일을 선택해주세요")

        val categoryByLevelModel = basicProductService.getSubsidiaryMaterials(
            condition = SubsidiaryMaterialSearchCondition.subCondition(),
            page = PageRequest.ofSize(maxPageSize)
        ).flatMap {
            it.children.filter { it.label == defaultSubsidiaryMaterialName }
        }.firstOrNull() ?: throw  NoSuchElementException("디폴트 부자재 값이 없습니다.")

        val defaultSubsidiaryMaterialId = categoryByLevelModel.value!!

        val partnerModelMap =
            partnerApiClient.loadAllPartners().payload!!.associateBy { it.memberId }

        val wb = ExcelReadUtils.extractSimple(fileName, file.inputStream)
        excelMapper.toBasicProductExcelModel(wb).forEach {
            val partnerId = partnerModelMap[it.memberId]!!.partnerId
            basicProductService.createBasicProduct(
                it.toBasicProductDetailCreateModel(defaultSubsidiaryMaterialId, partnerId)
            )
        }
    }

    @Transactional
    fun updateProductCode(fileName: String?, file: MultipartFile?): Any {
        TODO("Not yet implemented")
    }


}
