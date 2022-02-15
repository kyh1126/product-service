package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.apiclient.PartnerApiClient
import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.request.CommonCreateBulkModel
import com.smartfoodnet.apiclient.request.PreSalesProductModel
import com.smartfoodnet.apiclient.request.PreShippingProductSimpleModel
import com.smartfoodnet.fnproduct.product.mapper.BasicProductExcelModelMapper
import com.smartfoodnet.fnproduct.product.model.BasicProductExcelModel
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
    private val wmsApiClient: WmsApiClient
) {
    private val defaultSubsidiaryMaterialName = "없음"

    @Transactional
    fun excelToBasicProducts(fileName: String?, file: MultipartFile?) {
        val defaultSubsidiaryMaterialId = getDefaultSubsidiaryMaterialId()
        val partnerModelMap =
            partnerApiClient.loadAllPartners().payload!!.associateBy { it.memberId }

        getBasicProductExcelModel(fileName, file).forEach {
            basicProductService.createBasicProductWithNosnosMigration(
                it.toBasicProductDetailCreateModel(
                    defaultSubsidiaryMaterialId,
                    partnerModelMap[it.memberId]!!
                )
            )
        }
    }

    @Transactional
    fun updateProductCode(fileName: String?, file: MultipartFile?) {
        val basicProductExcelModels = getBasicProductExcelModel(fileName, file)
        val memberId = basicProductExcelModels.first().memberId
        val basicProducts = basicProductExcelModels.map {
            basicProductService.updateProductCode(it.shippingProductId)
        }

        // nosnos 쪽 출고상품 productCode 업데이트
        wmsApiClient.updateShippingProducts(
            CommonCreateBulkModel(
                memberId = memberId,
                requestDataList = basicProducts.map {
                    PreShippingProductSimpleModel.fromEntity(it)
                }
            )
        )
    }

    @Transactional
    fun createNosnosSalesProducts(fileName: String?, file: MultipartFile?) {
        val basicProductExcelModels = getBasicProductExcelModel(fileName, file)
        val memberId = basicProductExcelModels.first().memberId
        val shippingProductIds = basicProductExcelModels.map { it.shippingProductId }

        val basicProducts =
            basicProductService.getBasicProductsByShippingProductId(shippingProductIds)

        val basicProductsBySalesProductCode = basicProducts.associateBy { it.salesProductCode }

        // nosnos 쪽 판매상품 일괄 등록
        val postSalesProductModels = wmsApiClient.createSalesProducts(
            CommonCreateBulkModel(
                memberId = memberId,
                requestDataList = basicProducts.map {
                    PreSalesProductModel.fromEntity(it)
                }
            )
        ).payload?.processedDataList

        // 판매상품 id 업데이트
        postSalesProductModels?.forEach {
            val basicProduct = basicProductsBySalesProductCode[it.salesProductCode]
            basicProduct?.updateSalesProductId(it.salesProductId)
        }
    }

    private fun getBasicProductExcelModel(
        fileName: String?,
        file: MultipartFile?
    ): List<BasicProductExcelModel> {
        if (file == null) throw IllegalArgumentException("엑셀 파일을 선택해주세요")

        val wb = ExcelReadUtils.extractSimple(fileName, file.inputStream)
        return excelMapper.toBasicProductExcelModel(wb)
    }

    private fun getDefaultSubsidiaryMaterialId(): Long {
        val categoryByLevelModel = basicProductService.getSubsidiaryMaterials(
            condition = SubsidiaryMaterialSearchCondition.subCondition(),
            page = PageRequest.ofSize(maxPageSize)
        ).flatMap {
            it.children.filter { it.label == defaultSubsidiaryMaterialName }
        }.firstOrNull() ?: throw NoSuchElementException("디폴트 부자재 값이 없습니다.")

        return categoryByLevelModel.value!!
    }
}
