package com.smartfoodnet.fnproduct.product.mapper

import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.product.model.BasicProductExcelModel
import com.smartfoodnet.fnproduct.product.model.ExpirationDateInfoExcelModel
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import org.springframework.stereotype.Component
import sfn.excel.module.workbook.read.models.SimpleWorkbookModels

@Component
class BasicProductExcelModelMapper {

    fun toBasicProductExcelModel(workbook: SimpleWorkbookModels.Workbook): List<BasicProductExcelModel> {
        val worksheet = workbook.worksheets[0]
        return toBasicProductExcelModel(worksheet)
    }

    // worksheets[0]
    // name: "Sheet1", sheetIndex: 0, columnDataTypes[:8]: CellType.STRING, columnDataTypes[8:]: CellType.BLANK
    // rows:
    // [
    //      ["member_id", "shipping_product_id", "출고 상품명(product_name)", "바코드(upc)",      "관리키워드2(manage_code2)", "낱개 입수(single_eta)", "팔레트 입수(palet_count)", "유통기한 사용(use_expire_date)", "제조일자 사용(use_make_date)", "유통기한(expire_date_by_make_date)", "활성화(status)"],
    //      ["",          "",                    "양반 백합미역국 ",           "8801047169413",  "상온",                     "20",                  "",                      "1",                           "0",                        "",                                 "1"],
    // ]
    private fun toBasicProductExcelModel(worksheet: SimpleWorkbookModels.Worksheet): List<BasicProductExcelModel> {
        var memberIdIdx = Int.MAX_VALUE
        var shippingProductIdIdx = Int.MAX_VALUE
        var productCodeIdx = Int.MAX_VALUE
        var productNameIdx = Int.MAX_VALUE
        var barcodeIdx = Int.MAX_VALUE
        var handlingTemperatureIdx = Int.MAX_VALUE
        var piecesPerBoxIdx = Int.MAX_VALUE
        var piecesPerPaletteIdx = Int.MAX_VALUE
        var expirationDateWriteYnIdx = Int.MAX_VALUE
        var manufactureDateWriteYnIdx = Int.MAX_VALUE
        var expirationDateIdx = Int.MAX_VALUE
        var activeYnIdx = Int.MAX_VALUE

        val titles = worksheet.rows[0]
        titles.forEach {
            when (it) {
                "member_id" -> memberIdIdx = titles.indexOf(it)
                "shipping_product_id" -> shippingProductIdIdx = titles.indexOf(it)
                "product_code" -> productCodeIdx = titles.indexOf(it)
                "product_name" -> productNameIdx = titles.indexOf(it)
                "upc" -> barcodeIdx = titles.indexOf(it)
                "manage_code2" -> handlingTemperatureIdx = titles.indexOf(it)
                "single_eta" -> piecesPerBoxIdx = titles.indexOf(it)
                "palet_count" -> piecesPerPaletteIdx = titles.indexOf(it)
                "use_expire_date" -> expirationDateWriteYnIdx = titles.indexOf(it)
                "use_make_date" -> manufactureDateWriteYnIdx = titles.indexOf(it)
                "expire_date_by_make_date" -> expirationDateIdx = titles.indexOf(it)
                "status" -> activeYnIdx = titles.indexOf(it)
            }
        }

        val result = mutableListOf<BasicProductExcelModel>()
        for (i in 1 until worksheet.rows.size) {
            val row = worksheet.rows[i]
            try {
                result += BasicProductExcelModel(
                    memberId = validateNumberFormat(
                        "화주(고객사) ID", convertToLong(row[memberIdIdx])
                    ),
                    shippingProductId = validateNumberFormat(
                        "출고상품 ID", convertToLong(row[shippingProductIdIdx])
                    ),
                    productName = row[productNameIdx],
                    barcode = row[barcodeIdx],
                    handlingTemperature = convertToHandlingTemperature(row[handlingTemperatureIdx]),
                    piecesPerBox = validateNumberFormat(
                        "박스입수", convertToInt(row[piecesPerBoxIdx])
                    ),
                    piecesPerPalette = convertToInt(row[piecesPerPaletteIdx]),
                    expirationDateInfoExcelModel = ExpirationDateInfoExcelModel(
                        manufactureDateWriteYn = convertToYN(row[manufactureDateWriteYnIdx]),
                        expirationDateWriteYn = convertToYN(row[expirationDateWriteYnIdx]),
                        manufactureToExpirationDate = convertToInt(row[expirationDateIdx])
                    ),
                    activeYn = convertToYN(row[activeYnIdx])
                )
            } catch (e: Exception) {
                log.error(
                    "BasicProductExcelModel 생성 실패, row idx: ${i}, productCode: ${row[productCodeIdx]}",
                    e
                )
            }
        }
        return result
    }

    private fun convertToYN(target: String?): String =
        when (target?.trim()) {
            "1" -> "Y"
            "0", null -> "N"
            else -> throw IllegalArgumentException("YN 형식이 아닌 값이 입력되었습니다. target: ${target}")
        }

    private fun <T : Number> validateNumberFormat(name: String, target: T?): T =
        target ?: throw IllegalArgumentException("${name}은 숫자만 입력해주세요.")

    private fun convertToLong(target: String): Long? = target.toLongOrNull()

    private fun convertToInt(target: String): Int? = target.toIntOrNull()

    private fun convertToHandlingTemperature(target: String): HandlingTemperatureType =
        HandlingTemperatureType.fromDesc(target)
            ?: throw IllegalArgumentException("유효한 취급온도가 아닙니다.")

    companion object : Log
}
