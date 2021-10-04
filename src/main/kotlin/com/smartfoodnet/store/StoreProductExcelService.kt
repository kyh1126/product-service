package com.smartfoodnet.store

import com.google.common.collect.Lists
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.store.model.StoreProductModel
import com.smartfoodnet.store.support.StoreProductHeader
import com.smartfoodnet.store.support.StoreProductHeader.*
import com.smartfoodnet.store.support.StoreProductHeaderIndexMap
import com.smartfoodnet.store.support.StoreProductRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import sfn.excel.module.workbook.read.ExcelReadUtils
import sfn.excel.module.workbook.read.models.SimpleWorkbookModels

@Service
class StoreProductExcelService(var storeProductRepository: StoreProductRepository, var basicProductRepository: BasicProductRepository) {
    private final val HEADER_ROW_INDEX: Int = 1
    private final val WORKSHEET_INDEX: Int = 0
    private final val DATA_STARTING_ROW_INDEX: Int = 1

    fun createBulkByExcelFile(file: MultipartFile, partnerId: Long): List<StoreProductModel> {
        val workbook = ExcelReadUtils.extractSimple(file.originalFilename, file.inputStream)
        val worksheet = workbook.worksheets[WORKSHEET_INDEX]

        val storeProductModels = buildStoreProductModels(worksheet, partnerId)
        val storeProducts = storeProductModels.map{
            val storeProduct = it.toEntity()
            it.basicProductCode ?: run {
                val basicProduct = basicProductRepository.findByCode(it.basicProductCode!!)
                storeProduct.basicProduct = basicProduct
            }

            storeProduct
        }

        return storeProductRepository.saveAll(storeProducts).map{ StoreProductModel.from(it) }
    }

    private fun buildStoreProductModels(worksheet: SimpleWorkbookModels.Worksheet, partnerId: Long): List<StoreProductModel>{
        val rows = worksheet.rows.subList(DATA_STARTING_ROW_INDEX, worksheet.rows.size)
        val indexMap = StoreProductHeaderIndexMap.from(rows[HEADER_ROW_INDEX])
        val storeProductModels = Lists.newArrayList<StoreProductModel>()
        rows.forEach{
            storeProductModels.add(buildModelFromRow(it, indexMap, partnerId))
        }

        return storeProductModels
    }

    private fun buildModelFromRow(row: List<String>, map: Map<StoreProductHeader, Int>, partnerId: Long): StoreProductModel {
        return StoreProductModel(
            storeName = row[map[STORE_NAME]?: 0],
            storeCode = row[map[STORE_CODE]?: 0],
            storeProductCode = row[map[STORE_PRODUCT_CODE]?: 0],
            name = row[map[NAME]?: 0],
            optionCode = row[map[OPTION_CODE]?: 0],
            optionName = row[map[OPTION_NAME]?: 0],
            basicProductCode = row[map[BASIC_PRODUCT_CODE]?: 0],
            basicProductName = row[map[BASIC_PRODUCT_NAME]?: 0],
            partnerId = partnerId
        )
    }

}