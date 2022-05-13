package com.smartfoodnet.fnproduct.migration

import com.smartfoodnet.apiclient.PartnerApiClient
import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.request.*
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.NosnosShippingProductModel
import com.smartfoodnet.common.Constants.NOSNOS_INITIAL_PAGE
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.migration.dto.NosnosShippingProductTestModel
import com.smartfoodnet.fnproduct.product.BasicProductService
import com.smartfoodnet.fnproduct.product.ShippingProductArchiveRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
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
@Transactional
class MigrationService(
    @Value("\${spring.data.web.pageable.max-page-size:50}")
    private val maxPageSize: Int,
    private val excelMapper: BasicProductExcelModelMapper,
    private val basicProductService: BasicProductService,
    private val shippingProductArchiveRepository: ShippingProductArchiveRepository,
    private val partnerApiClient: PartnerApiClient,
    private val wmsApiClient: WmsApiClient
) {
    private val defaultSubsidiaryMaterialName = "없음"
    private val nosnosMaxRequestSize = 100

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

    fun updateProductCodes(fileName: String?, file: MultipartFile?, startIdx: Int?, endIdx: Int?) {
        val basicProductExcelModels = getBasicProductExcelModel(fileName, file, startIdx, endIdx)
        val rowIdxList = basicProductExcelModels.associate { it.shippingProductId to it.rowIdx }

        val basicProductsByPartnerId = basicProductExcelModels.map {
            basicProductService.updateProductCode(it.shippingProductId)
        }.groupBy { it.partnerId }

        // nosnos 쪽 출고상품 productCode 업데이트
        basicProductsByPartnerId.entries.forEach { (partnerId, basicProducts) ->
            log.info("[updateProductCodes] partnerId: $partnerId start!")

            var start = 0
            while (start < basicProducts.size) {
                val end = min(start + nosnosMaxRequestSize, basicProducts.size)
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
            basicProductService.getBasicProductsByShippingProductIds(shippingProductIds)
                .groupBy { it.partnerId }

        // nosnos 쪽 판매상품 일괄 등록, 판매상품 id 업데이트
        basicProductsByPartnerId.entries.forEach { (partnerId, basicProducts) ->
            log.info("[createNosnosSalesProducts] partnerId: $partnerId start!")

            var start = 0
            while (start < basicProducts.size) {
                val end = min(start + nosnosMaxRequestSize, basicProducts.size)
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
            basicProductService.getBasicProductsByShippingProductIds(shippingProductIds)
                .groupBy { it.partnerId }

        // nosnos 쪽 기존 출고상품-판매상품 연결
        basicProductsByPartnerId.entries.forEach { (partnerId, basicProducts) ->
            log.info("[createProductMappings] partnerId: $partnerId start!")

            var start = 0
            while (start < basicProducts.size) {
                val end = min(start + nosnosMaxRequestSize, basicProducts.size)
                val subList = basicProducts.subList(start, end)
                log.info("start: $start, end: $end, rowIdx: ${subList.map { rowIdxList[it.shippingProductId] }}")

                // nosnos 쪽 상품연결 정보 일괄 등록
                wmsApiClient.createProductMappings(
                    CommonCreateBulkModel(
                        partnerId = partnerId,
                        requestDataList = basicProducts.map { PreProductMappingModel.fromEntity(it) }
                    )
                )

                start = end
            }
        }
    }

    /**
     * Step 0
     */
    fun archiveShippingProducts(memberId: Long, startPage: Int, endPage: Int) {
        val partnerModel = partnerApiClient.getPartner(memberId).payload!!

        var page = startPage
        var totalPage = endPage

        while (page <= totalPage) {
            val model: CommonDataListModel<NosnosShippingProductModel>
            try {
                model = wmsApiClient.getShippingProducts(
                    BasicProductReadModel(memberId = memberId, status = 1, page = page)
                ).payload!!
            } catch (e: Exception) {
                log.error("[archiveShippingProducts] page: $page", e)
                throw BaseRuntimeException(errorMessage = "출고상품 조회 실패, memberId: ${memberId}, page: ${page}")
            }

            if (totalPage == NOSNOS_INITIAL_PAGE) {
                totalPage = model.totalPage.toInt()
            }
            val dataList = model.dataList

            shippingProductArchiveRepository.saveAll(
                dataList.map { it.toShippingProductArchive(partnerModel.partnerId) }
            )

            page++
        }
    }

    /**
     * Step 1
     */
    fun nosnosToBasicProducts(memberId: Long, startPage: Int, endPage: Int, isTest: Boolean) {
        val defaultSubsidiaryMaterialId = getDefaultSubsidiaryMaterialId()
        val partnerModel = partnerApiClient.getPartner(memberId).payload!!

        var page = startPage
        var totalPage = endPage

        while (page <= totalPage) {
            val model: CommonDataListModel<NosnosShippingProductModel>
            try {
                model = wmsApiClient.getShippingProducts(
                    BasicProductReadModel(memberId = memberId, status = 1, page = page)
                ).payload!!
            } catch (e: Exception) {
                log.error("[nosnosToBasicProducts] page: $page", e)
                throw BaseRuntimeException(errorMessage = "출고상품 생성 실패, memberId: ${memberId}, page: ${page}")
            }

            if (totalPage == NOSNOS_INITIAL_PAGE) {
                totalPage = model.totalPage.toInt()
            }
            val dataList = model.dataList

            dataList.forEach {
                basicProductService.createBasicProductWithNosnosMigration(
                    when (isTest) {
                        true -> NosnosShippingProductTestModel().toBasicProductDetailCreateModel(
                            it,
                            defaultSubsidiaryMaterialId,
                            partnerModel
                        )
                        false -> it.toBasicProductDetailCreateModel(
                            it,
                            defaultSubsidiaryMaterialId,
                            partnerModel
                        )
                    }
                )
            }
            page++
        }
    }

    /**
     * Step 2
     */
    fun updateProductCodes(memberId: Long, shippingProductIds: List<Long>?) {
        val basicProducts: List<BasicProduct>
        val partnerId = partnerApiClient.getPartner(memberId).payload!!.partnerId

        basicProducts =
            if (shippingProductIds.isNullOrEmpty()) basicProductService.getBasicProductsByPartnerId(partnerId)
            else basicProductService.getBasicProductsByShippingProductIds(shippingProductIds)

        // nosnos 쪽 출고상품 productCode 업데이트
        basicProducts.chunked(100).forEach { targetBasicProducts ->
            try {
                wmsApiClient.updateShippingProducts(
                    CommonCreateBulkModel(
                        partnerId = partnerId,
                        requestDataList = targetBasicProducts.map(PreShippingProductSimpleModel::fromEntity)
                    )
                )
                targetBasicProducts.forEach { it.updateProductCode(it.code!!) }
            } catch (e: Exception) {
                log.error("[updateProductCodes] shippingProductIds: ${targetBasicProducts.map { it.shippingProductId }}", e)
            }
        }
    }

    /**
     * Step 3
     */
    fun createNosnosSalesProducts(memberId: Long, shippingProductIds: List<Long>?) {
        val basicProducts: List<BasicProduct>
        val partnerId = partnerApiClient.getPartner(memberId).payload!!.partnerId

        basicProducts =
            if (shippingProductIds.isNullOrEmpty()) basicProductService.getBasicProductsByPartnerId(partnerId)
            else basicProductService.getBasicProductsByShippingProductIds(shippingProductIds)

        val basicProductBySalesProductCode = basicProducts.associateBy { it.salesProductCode }

        // nosnos 쪽 판매상품 일괄 등록
        basicProducts.chunked(100).forEach { targetBasicProducts ->
            try {
                val postSalesProductModels = wmsApiClient.createSalesProducts(
                    CommonCreateBulkModel(
                        partnerId = partnerId,
                        requestDataList = targetBasicProducts.map(PreSalesProductModel::fromEntity)
                    )
                ).payload?.processedDataList

                // 판매상품 id 업데이트
                postSalesProductModels?.forEach {
                    val basicProduct = basicProductBySalesProductCode[it.salesProductCode]
                    basicProduct?.updateSalesProductId(it.salesProductId)
                }
            } catch (e: Exception) {
                log.error("[createNosnosSalesProducts] shippingProductIds: ${targetBasicProducts.map { it.shippingProductId }}", e)
            }
        }
    }

    /**
     * Step 4
     */
    fun createProductMappings(memberId: Long, shippingProductIds: List<Long>?) {
        val basicProducts: List<BasicProduct>
        val partnerId = partnerApiClient.getPartner(memberId).payload!!.partnerId

        basicProducts =
            if (shippingProductIds.isNullOrEmpty()) basicProductService.getBasicProductsByPartnerId(partnerId)
            else basicProductService.getBasicProductsByShippingProductIds(shippingProductIds)

        // nosnos 쪽 상품연결 정보 일괄 등록
        basicProducts.chunked(100).forEach { targetBasicProducts ->
            try {
                wmsApiClient.createProductMappings(
                    CommonCreateBulkModel(
                        partnerId = partnerId,
                        requestDataList = targetBasicProducts.map(PreProductMappingModel::fromEntity)
                    )
                )
            } catch (e: Exception) {
                log.error("[createNosnosSalesProducts] shippingProductIds: ${targetBasicProducts.map { it.shippingProductId }}", e)
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
