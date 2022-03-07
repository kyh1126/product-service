package com.smartfoodnet.fnproduct.store

import com.smartfoodnet.apiclient.OrderManagementServiceApiClient
import com.smartfoodnet.apiclient.response.StoreInfoModel
import com.smartfoodnet.common.Constants.DATA_STARTING_ROW_INDEX
import com.smartfoodnet.common.Constants.HEADER_ROW_INDEX
import com.smartfoodnet.common.Constants.WORKSHEET_INDEX
import com.smartfoodnet.common.error.exception.ExternalApiError
import com.smartfoodnet.common.error.exception.NoSuchElementError
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import com.smartfoodnet.fnproduct.store.entity.StoreProductMapping
import com.smartfoodnet.fnproduct.store.model.request.StoreProductBulkCreateModel
import com.smartfoodnet.fnproduct.store.model.response.StoreProductModel
import com.smartfoodnet.fnproduct.store.support.StoreProductHeader
import com.smartfoodnet.fnproduct.store.support.StoreProductHeader.BASIC_PRODUCT_CODE
import com.smartfoodnet.fnproduct.store.support.StoreProductHeader.BASIC_PRODUCT_QUANTITY
import com.smartfoodnet.fnproduct.store.support.StoreProductHeader.NAME
import com.smartfoodnet.fnproduct.store.support.StoreProductHeader.OPTION_CODE
import com.smartfoodnet.fnproduct.store.support.StoreProductHeader.OPTION_NAME
import com.smartfoodnet.fnproduct.store.support.StoreProductHeader.STORE_NAME
import com.smartfoodnet.fnproduct.store.support.StoreProductHeader.STORE_PRODUCT_CODE
import com.smartfoodnet.fnproduct.store.support.StoreProductHeaderIndexMap
import com.smartfoodnet.fnproduct.store.support.StoreProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sfn.excel.module.workbook.read.ExcelReadUtils
import sfn.excel.module.workbook.read.models.SimpleWorkbookModels

@Service
@Transactional(readOnly = true)
class StoreProductExcelService(
    private val storeProductRepository: StoreProductRepository,
    private val basicProductRepository: BasicProductRepository,
    private val orderManagementServiceApiClient: OrderManagementServiceApiClient
) {

    @Transactional
    //TODO: basicProduct 추가로직 생성

    fun createBulkByExcelFile(file: MultipartFile, partnerId: Long): List<StoreProductModel> {
        val workbook = ExcelReadUtils.extractSimple(file.originalFilename, file.inputStream)
        val worksheet = workbook.worksheets[WORKSHEET_INDEX]

        val storeProducts = buildStoreProducts(worksheet, partnerId)

        return storeProductRepository.saveAll(storeProducts).map(StoreProductModel::from)
    }

    private fun buildStoreProducts(
        worksheet: SimpleWorkbookModels.Worksheet,
        partnerId: Long
    ): List<StoreProduct> {
        val indexMap = StoreProductHeaderIndexMap.from(worksheet.rows[HEADER_ROW_INDEX])
        val rows = worksheet.rows.subList(DATA_STARTING_ROW_INDEX, worksheet.rows.size)
        val storeProducts = mutableListOf<StoreProduct>()

        val storeInfos = orderManagementServiceApiClient.getStoreInfos(partnerId = partnerId).payload ?: throw ExternalApiError("쇼핑몰 정보를 가져오는데 실패했습니다. 잠시 후 다시 시도해 주시기 바랍니다.")

        rows.forEach { row ->
            buildModelFromRow(row, indexMap, partnerId, storeInfos)?.let { model ->
                val existingStoreProduct = storeProducts.firstOrNull { storeProduct ->
                    isSameStoreProduct(storeProduct, model)
                }

                if(existingStoreProduct != null) {
                    val storeProductMapping = buildStoreProductMapping(model, existingStoreProduct)

                    storeProductMapping?.let { existingStoreProduct.storeProductMappings.add(it) }
                } else {
                    val storeProduct = model.toEntity()
                    val storeProductMapping = buildStoreProductMapping(model, storeProduct)

                    storeProductMapping?.let { storeProduct.storeProductMappings.add(it) }

                    storeProducts.add(storeProduct)
                }
            }
        }

        return storeProducts
    }

    private fun buildStoreProductMapping(model: StoreProductBulkCreateModel, storeProduct: StoreProduct): StoreProductMapping? {
        if(model.basicProductCode == null) return null

        val basicProduct = basicProductRepository.findByCode(model.basicProductCode) ?: throw NoSuchElementError("기본상품이 존재하지 않습니다.")

        return StoreProductMapping(
            basicProduct = basicProduct,
            storeProduct = storeProduct,
            quantity = model.basicProductQuantity ?: 1
        )
    }

    private fun isSameStoreProduct(storeProduct: StoreProduct, model2: StoreProductBulkCreateModel): Boolean {
        return (storeProduct.storeId == model2.storeId)
                && (storeProduct.storeProductCode == model2.storeProductCode)
                && (storeProduct.name == model2.name)
                && (storeProduct.optionCode == model2.optionCode)
                && (storeProduct.optionName == model2.optionName)
    }

    private fun buildModelFromRow(
        row: List<String>,
        map: Map<StoreProductHeader, Int>,
        partnerId: Long,
        storeInfos: List<StoreInfoModel>
    ): StoreProductBulkCreateModel? {
        if (row[map[NAME]!!] == "") {
            return null
        }

        val storeInfo = storeInfos.firstOrNull { it.storeName ==  row[map[STORE_NAME]!!]} ?: throw NoSuchElementError("쇼핑몰 정보를 찾을 수 없습니다. [쇼핑몰 명: ${row[map[STORE_NAME]!!]}]")

        val basicProductCode = if(row[map[BASIC_PRODUCT_CODE]!!] == "") null else row[map[BASIC_PRODUCT_CODE]!!]
        val basicProductQuantity = try { row[map[BASIC_PRODUCT_QUANTITY]!!].toInt() } catch (e: NumberFormatException) { null }

        return StoreProductBulkCreateModel(
            storeName = storeInfo.storeName,
            storeId = storeInfo.storeId,
            storeIcon = storeInfo.icon,
            storeProductCode = row[map[STORE_PRODUCT_CODE]!!],
            name = row[map[NAME]!!],
            optionCode = row[map[OPTION_CODE]!!],
            optionName = row[map[OPTION_NAME]!!],
            basicProductCode = basicProductCode,
            basicProductQuantity = basicProductQuantity,
            partnerId = partnerId
        )
    }
}
