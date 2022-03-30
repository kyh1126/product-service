package com.smartfoodnet.fnproduct.order.vo

enum class OrderUploadType(
    val description: String,
    val descriptionKr: String
) {
    API("API", "API"),
    UPLOAD("EXCEL", "엑셀"),
    MANUAL("MANUAL", "수동"),
}