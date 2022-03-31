package com.smartfoodnet.fnproduct.order.vo

enum class OrderUploadType(
    val description: String
) {
    API("API"),
    UPLOAD("업로드"), // 엑셀 업로드
    MANUAL("수동"), // 수동으로 출고할 경우(주문외출고)
}