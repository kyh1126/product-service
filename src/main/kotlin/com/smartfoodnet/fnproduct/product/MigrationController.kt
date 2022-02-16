package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.model.response.CommonResponse
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Api(description = "마이그레이션 관련 API")
@RestController
@RequestMapping("migrations")
class MigrationController(private val migrationService: MigrationService) {

    @Operation(summary = "[Step 1] 출고상품 엑셀로 기본상품 생성 작업")
    @PostMapping(value = ["excel/basic-products"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun excelToBasicProducts(
        @Parameter(description = "파일이름")
        @RequestParam("fileName", required = false) fileName: String?,
        @Parameter(name = "file", description = "파일")
        @RequestPart("file", required = false) file: MultipartFile?
    ): CommonResponse<String> {
        migrationService.excelToBasicProducts(fileName, file)
        return CommonResponse(HttpStatus.OK.reasonPhrase)
    }

    @Operation(summary = "[Step 2] 출고상품 엑셀로 출고상품 product-code 업데이트 작업")
    @PostMapping(value = ["excel/product-codes"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProductCodes(
        @Parameter(description = "파일이름")
        @RequestParam("fileName", required = false) fileName: String?,
        @Parameter(name = "file", description = "파일")
        @RequestPart("file", required = false) file: MultipartFile?,
        @Parameter(description = "시작 Row idx - 1 (헤더제외)")
        @RequestParam("startIdx", required = false) startIdx: Int?,
        @Parameter(description = "끝 Row idx - 1 (헤더제외)")
        @RequestParam("endIdx", required = false) endIdx: Int?
    ): CommonResponse<String> {
        migrationService.updateProductCodes(fileName, file, startIdx, endIdx)
        return CommonResponse(HttpStatus.OK.reasonPhrase)
    }

    @Operation(summary = "[Step 3] 출고상품 엑셀로 판매상품 생성 작업")
    @PostMapping(value = ["excel/sales-products"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createNosnosSalesProducts(
        @Parameter(description = "파일이름")
        @RequestParam("fileName", required = false) fileName: String?,
        @Parameter(name = "file", description = "파일")
        @RequestPart("file", required = false) file: MultipartFile?,
        @Parameter(description = "시작 Row idx - 1 (헤더제외)")
        @RequestParam("startIdx", required = false) startIdx: Int?,
        @Parameter(description = "끝 Row idx - 1 (헤더제외)")
        @RequestParam("endIdx", required = false) endIdx: Int?
    ): CommonResponse<String> {
        migrationService.createNosnosSalesProducts(fileName, file, startIdx, endIdx)
        return CommonResponse(HttpStatus.OK.reasonPhrase)
    }

    @Operation(summary = "[Step 4] 출고상품 엑셀로 기존 출고상품-판매상품 연결 작업")
    @PostMapping(value = ["excel/mappings"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createProductMappings(
        @Parameter(description = "파일이름")
        @RequestParam("fileName", required = false) fileName: String?,
        @Parameter(name = "file", description = "파일")
        @RequestPart("file", required = false) file: MultipartFile?,
        @Parameter(description = "시작 Row idx - 1 (헤더제외)")
        @RequestParam("startIdx", required = false) startIdx: Int?,
        @Parameter(description = "끝 Row idx - 1 (헤더제외)")
        @RequestParam("endIdx", required = false) endIdx: Int?
    ): CommonResponse<String> {
        migrationService.createProductMappings(fileName, file, startIdx, endIdx)
        return CommonResponse(HttpStatus.OK.reasonPhrase)
    }
}
