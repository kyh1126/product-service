package com.smartfoodnet.fnproduct.store

import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.store.support.StoreProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StoreProductExcelService(
    var storeProductRepository: StoreProductRepository,
    var basicProductRepository: BasicProductRepository
) {
/*
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
        val storeProductModels = mutableListOf<StoreProductCreateModel>()

        rows.forEach { row ->
            buildModelFromRow(row, indexMap, partnerId)?.let {
                storeProductModels.add(it)
            }
        }

        return storeProductModels
    }

    private fun buildModelFromRow(
        row: List<String>,
        map: Map<StoreProductHeader, Int>,
        partnerId: Long
    ): StoreProductCreateModel? {
        if (row[map[NAME]!!] == "") {
            return null
        }

        return StoreProductCreateModel(
            storeName = "naver",
            storeCode = "code",
            storeProductCode = row[map[STORE_PRODUCT_CODE]!!],
            name = row[map[NAME]!!],
            optionCode = row[map[OPTION_CODE]!!],
            optionName = row[map[OPTION_NAME]!!],
            partnerId = partnerId
        )
    }

*/
}
