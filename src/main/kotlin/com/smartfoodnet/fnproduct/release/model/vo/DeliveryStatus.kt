package com.smartfoodnet.fnproduct.release.model.vo

enum class DeliveryStatus(
    val deliveryAgency: DeliveryAgency,
    val code: String,
    val desc: String,
) {
    // LOTTE
    PARCEL_COLLECTION(DeliveryAgency.LOTTE, "10", "출력"),
    PICK_UP_COMPLETED(DeliveryAgency.LOTTE, "12", "집하완료"),
    IN_PROGRESS_LOTTE(DeliveryAgency.LOTTE, "20", "발송"),
    ARRIVAL(DeliveryAgency.LOTTE, "21", "도착"),
    BEFORE_SHIPPING(DeliveryAgency.LOTTE, "40", "배달전"),
    COMPLETED_LOTTE(DeliveryAgency.LOTTE, "41", "배달완료"),

    // CJ
    PICK_UP(DeliveryAgency.CJ, "", "집화처리"),
    FORWARDING_COMPLETED(DeliveryAgency.CJ, "", "출고완료"),
    IN_PROGRESS_CJ(DeliveryAgency.CJ, "", "배송중"),
    COMPLETED_CJ(DeliveryAgency.CJ, "", "배송완료"),
}

/**
 * CJ 에서 제공하는 사이트 호출 결과의 scanNm(상태명) 상태코드
 *
 * @see     com.smartfoodnet.apiclient.CjDeliveryInfoApiClient.getDeliveryInfo(List<String>)
 */
enum class CjDeliveryStatus(val code: String, val desc: String) {
    PICK_UP("11", "집화처리"),
    UNLOAD("42", "간선하차"),
    LOAD("44", "간선상차"),
    IN_PROGRESS_CJ("82", "배달출발"),
    COMPLETED_CJ("91", "배달완료"),
}
