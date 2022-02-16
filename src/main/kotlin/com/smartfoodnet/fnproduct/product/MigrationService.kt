package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.apiclient.PartnerApiClient
import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.request.CommonCreateBulkModel
import com.smartfoodnet.apiclient.request.PreSalesProductModel
import com.smartfoodnet.apiclient.request.PreShippingProductSimpleModel
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.product.mapper.BasicProductExcelModelMapper
import com.smartfoodnet.fnproduct.product.model.BasicProductExcelModel
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialSearchCondition
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sfn.excel.module.workbook.read.ExcelReadUtils
import kotlin.math.min

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
    private val nosnosMaxRequestSize = 100

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
    fun updateProductCode(fileName: String?, file: MultipartFile?, startIdx: Int?, endIdx: Int?) {
        val basicProductExcelModels = getBasicProductExcelModel(fileName, file, startIdx, endIdx)
        val rowIdxList = basicProductExcelModels.associate { it.shippingProductId to it.rowIdx }

        val basicProductsByPartnerId = basicProductExcelModels.map {
            basicProductService.updateProductCode(it.shippingProductId)
        }.groupBy { it.partnerId }

        // nosnos 쪽 출고상품 productCode 업데이트
        basicProductsByPartnerId.entries.forEach { (partnerId, basicProducts) ->
            log.info("partnerId: $partnerId start!")

            var start = 0
            while (start < basicProducts.size) {
                val end = min(start, basicProducts.size)
                val subList = basicProducts.subList(start, end)
                log.info("start: $start, end: $end, rowIdx: ${subList.map { rowIdxList[it.shippingProductId] }}")

                wmsApiClient.updateShippingProducts(
                    CommonCreateBulkModel(
                        partnerId = partnerId,
                        requestDataList = subList.map {
                            PreShippingProductSimpleModel.fromEntity(it)
                        }
                    )
                )
                start = end
            }
        }
    }

    @Transactional
    fun createNosnosSalesProducts(
        fileName: String?,
        file: MultipartFile?,
        startIdx: Int?,
        endIdx: Int?
    ) {
        val basicProductExcelModels = getBasicProductExcelModel(fileName, file)
        val rowIdxList = basicProductExcelModels.associate { it.shippingProductId to it.rowIdx }

        val shippingProductIds = basicProductExcelModels.map { it.shippingProductId }
        val basicProductsByPartnerId =
            basicProductService.getBasicProductsByShippingProductId(shippingProductIds)
                .groupBy { it.partnerId }

        // nosnos 쪽 판매상품 일괄 등록, 판매상품 id 업데이트
        basicProductsByPartnerId.entries.forEach { (partnerId, basicProducts) ->
            log.info("partnerId: $partnerId start!")

            var start = 0
            while (start < basicProducts.size) {
                val end = min(start, basicProducts.size)
                val subList = basicProducts.subList(start, end)
                log.info("start: $start, end: $end, rowIdx: ${subList.map { rowIdxList[it.shippingProductId] }}")

                val basicProductBySalesProductCode =
                    basicProducts.associateBy { it.salesProductCode }

                // nosnos 쪽 판매상품 일괄 등록
                val postSalesProductModels = wmsApiClient.createSalesProducts(
                    CommonCreateBulkModel(
                        partnerId = partnerId,
                        requestDataList = basicProducts.map { PreSalesProductModel.fromEntity(it) }
                    )
                ).payload?.processedDataList

                // 판매상품 id 업데이트
                postSalesProductModels?.forEach {
                    val basicProduct = basicProductBySalesProductCode[it.salesProductCode]
                    basicProduct?.updateSalesProductId(it.salesProductId)
                }
                start = end
            }
        }
    }

    @Transactional
    fun createProductMappings(
        fileName: String?,
        file: MultipartFile?,
        startIdx: Int?,
        endIdx: Int?
    ) {
        val basicProductExcelModels = getBasicProductExcelModel(fileName, file)
        val rowIdxList = basicProductExcelModels.associate { it.shippingProductId to it.rowIdx }

        val shippingProductIds = basicProductExcelModels.map { it.shippingProductId }
        val basicProductsByPartnerId =
            basicProductService.getBasicProductsByShippingProductId(shippingProductIds)
                .groupBy { it.partnerId }

        // nosnos 쪽 기존 출고상품-판매상품 연결
        basicProductsByPartnerId.entries.forEach { (partnerId, basicProducts) ->
            log.info("partnerId: $partnerId start!")

            var start = 0
            while (start < basicProducts.size) {
                val end = min(start, basicProducts.size)
                val subList = basicProducts.subList(start, end)
                log.info("start: $start, end: $end, rowIdx: ${subList.map { rowIdxList[it.shippingProductId] }}")

                // TODO: nosnos 쪽 상품연결 정보 등록(벌크)
                start = end
            }
        }
    }

    private fun getBasicProductExcelModel(
        fileName: String?,
        file: MultipartFile?,
        startIdx: Int? = 1,
        endIdx: Int? = null
    ): List<BasicProductExcelModel> {
        if (file == null) throw IllegalArgumentException("엑셀 파일을 선택해주세요")

        val wb = ExcelReadUtils.extractSimple(fileName, file.inputStream)
        val defaultEndIdx = wb.worksheets[0].rows.size - 1
        return excelMapper.toBasicProductExcelModel(wb, startIdx ?: 1, endIdx ?: defaultEndIdx)
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

    companion object : Log
}
