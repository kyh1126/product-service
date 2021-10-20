package com.smartfoodnet.store

import com.google.common.collect.Lists
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.store.model.StoreProductModel
import com.smartfoodnet.store.support.StoreProductHeader
import com.smartfoodnet.store.support.StoreProductHeader.*
import com.smartfoodnet.store.support.StoreProductHeaderIndexMap
import com.smartfoodnet.store.support.StoreProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sfn.excel.module.workbook.read.ExcelReadUtils
import sfn.excel.module.workbook.read.models.SimpleWorkbookModels

@Service
@Transactional(readOnly = true)
class StoreProductExcelService(var storeProductRepository: StoreProductRepository, var basicProductRepository: BasicProductRepository) {
    private val HEADER_ROW_INDEX: Int = 1
    private val WORKSHEET_INDEX: Int = 0
    private val DATA_STARTING_ROW_INDEX: Int = 3

    @Transactional
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
        val indexMap = StoreProductHeaderIndexMap.from(worksheet.rows[HEADER_ROW_INDEX])
        val rows = worksheet.rows.subList(DATA_STARTING_ROW_INDEX, worksheet.rows.size)
        val storeProductModels = Lists.newArrayList<StoreProductModel>()

        rows.forEach{ row ->
            buildModelFromRow(row, indexMap, partnerId)?.let {
                storeProductModels.add(it)
            }
        }

        return storeProductModels
    }

    private fun buildModelFromRow(row: List<String>, map: Map<StoreProductHeader, Int>, partnerId: Long): StoreProductModel? {
        if(row[map[NAME]!!] == "") {
            return null
        }

        return StoreProductModel(
            storeName = "naver",
            storeCode = "code",
            storeProductCode = row[map[STORE_PRODUCT_CODE]!!],
            name = row[map[NAME]!!],
            optionCode = row[map[OPTION_CODE]!!],
            optionName = row[map[OPTION_NAME]!!],
            basicProductCode = row[map[BASIC_PRODUCT_CODE]!!],
            basicProductName = row[map[BASIC_PRODUCT_NAME]!!],
            partnerId = partnerId
        )
    }
}