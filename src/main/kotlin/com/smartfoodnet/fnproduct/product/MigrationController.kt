package com.smartfoodnet.fnproduct.product

import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Api(description = "마이그레이션 관련 API")
@RestController
@RequestMapping("migrations")
class MigrationController(private val migrationService: MigrationService) {

    @Operation(summary = "[Step 1] 출고상품 엑셀로 기본상품 생성 작업")
    @PostMapping(value = ["excel/basic-product"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun excelToBasicProduct(
        @Parameter(description = "파일이름")
        @RequestParam("fileName", required = false) fileName: String?,
        @Parameter(name = "file", description = "파일")
        @RequestPart("file", required = false) file: MultipartFile?
    ) = migrationService.excelToBasicProduct(fileName, file)

    @Operation(summary = "[Step 2] 출고상품 엑셀로 출고상품 product-code 업데이트 작업")
    @PostMapping(value = ["excel/product-code"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProductCode(
        @Parameter(description = "파일이름")
        @RequestParam("fileName", required = false) fileName: String?,
        @Parameter(name = "file", description = "파일")
        @RequestPart("file", required = false) file: MultipartFile?
    ) = migrationService.updateProductCode(fileName, file)


}
