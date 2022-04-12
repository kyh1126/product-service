package com.smartfoodnet.fnproduct.claim.model.vo

/**
 * @see <a href="https://docs.google.com/spreadsheets/d/1PvcWod1Qq22IMXMoeMCrJvGDmHml-1LsCaM_d_8wfnc/edit#gid=1079462703">반품 사유 API</a>
 */
enum class ClaimReason(val returnReasonId: String?, val returnReason: String) {
    NONE("", "선택없음"),
    WRONG_DELIVERY_LOGISTICS("1", "오배송(물류사귀책)"),
    WRONG_DELIVERY_PARTNER("2", "오배송(고객사귀책)"),
    DELAYED_DELIVERY("3", "지연배송"),
    CHANGED_MIND("4", "고객변심"),
    POOR_QUALITY("5", "품질불량"),
    DAMAGED_PRODUCT("6", "상품파손"),
    BOX_BROKEN_ON_DELIVERY("7", "배송중 박스파손"),
    WRONG_PRODUCT_RELEASE("8", "상품오출고")
}
