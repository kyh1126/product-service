package com.smartfoodnet.fnproduct.store

import com.smartfoodnet.common.Constants.DATA_STARTING_ROW_INDEX
import com.smartfoodnet.common.Constants.HEADER_ROW_INDEX
import com.smartfoodnet.common.Constants.WORKSHEET_INDEX
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.model.request.BasicProductSearchCondition
import com.smartfoodnet.fnproduct.store.entity.QStoreProduct.storeProduct
import com.smartfoodnet.fnproduct.store.model.request.StoreProductBulkCreateModel
import com.smartfoodnet.fnproduct.store.model.request.StoreProductCreateModel
import com.smartfoodnet.fnproduct.store.model.response.StoreProductModel
import com.smartfoodnet.fnproduct.store.support.StoreProductHeader
import com.smartfoodnet.fnproduct.store.support.StoreProductHeader.*
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
    var storeProductRepository: StoreProductRepository,
    var basicProductRepository: BasicProductRepository
) {

    @Transactional
    //TODO: basicProduct 추가로직 생성

    fun createBulkByExcelFile(file: MultipartFile, partnerId: Long): List<StoreProductModel> {
        val workbook = ExcelReadUtils.extractSimple(file.originalFilename, file.inputStream)
        val worksheet = workbook.worksheets[WORKSHEET_INDEX]

        val storeProductModels = buildStoreProductModels(worksheet, partnerId)
        val storeProducts = storeProductModels.map {
//            val storeProduct = it.toEntity()
//            it.basicProductCode ?: run {
//                val basicProduct = basicProductRepository.findByCode(it.basicProductCode!!)
//                storeProduct.basicProduct = basicProduct
//            }

            storeProduct
        }

        return storeProductRepository.saveAll(storeProducts).map(StoreProductModel::from)
    }

    private fun buildStoreProductModels(
        worksheet: SimpleWorkbookModels.Worksheet,
        partnerId: Long
    ): List<StoreProductCreateModel> {
        val indexMap = StoreProductHeaderIndexMap.from(worksheet.rows[HEADER_ROW_INDEX])
        val rows = worksheet.rows.subList(DATA_STARTING_ROW_INDEX, worksheet.rows.size)
        val storeProductModels = mutableListOf<StoreProductBulkCreateModel>()

        rows.forEach { row ->
            buildModelFromRow(row, indexMap, partnerId)?.let { newModel ->
                val existingModel = storeProductModels.first { model ->
                    isSameStoreProduct(model, newModel)
                }

                if(existingModel != null) {
                    
                }
                storeProductModels.add(it)
            }
        }

        return storeProductModels
    }

    private fun isSameStoreProduct(model1: StoreProductBulkCreateModel, model2: StoreProductBulkCreateModel): Boolean {
        return (model1.storeId == model2.storeId)
                && (model1.storeProductCode == model2.storeProductCode)
                && (model1.name == model2.name)
                && (model1.optionCode == model2.optionCode)
                && (model1.optionName == model2.optionName)
    }

    private fun buildModelFromRow(
        row: List<String>,
        map: Map<StoreProductHeader, Int>,
        partnerId: Long
    ): StoreProductBulkCreateModel? {
        if (row[map[NAME]!!] == "") {
            return null
        }

        return StoreProductBulkCreateModel(
            storeName = row[map[STORE_NAME]!!],
            storeId = row[map[STORE_ID]!!].toLong(),
            storeProductCode = row[map[STORE_PRODUCT_CODE]!!],
            name = row[map[NAME]!!],
            optionCode = row[map[OPTION_CODE]!!],
            optionName = row[map[OPTION_NAME]!!],
            partnerId = partnerId
        )
    }


}
