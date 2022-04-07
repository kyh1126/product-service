package com.smartfoodnet.fnproduct.release.model.vo

enum class DeliveryAgency(val playAutoName: String, val searchKeyword: String) {
    LOTTE("롯데택배", "롯데"),
    CJ("CJ 대한통운", "CJ");

    companion object {
        fun getDeliveryAgencyByName(name: String?): DeliveryAgency? {
            return values().firstOrNull { name?.contains(it.searchKeyword, true) == true }
        }
    }
}

enum class DeliveryStatus(
    val deliveryAgency: DeliveryAgency,
    val code: String,
    val desc: String,
) {
    // LOTTE
    PARCEL_COLLECTION(DeliveryAgency.LOTTE, "10", "출력"),
    SHIPPING_CODE_REGISTERED(DeliveryAgency.LOTTE, "12", "집하완료"),
    IN_PROGRESS_LOTTE(DeliveryAgency.LOTTE, "20", "발송"),
    ARRIVAL(DeliveryAgency.LOTTE, "21", "도착"),
    BEFORE_SHIPPING(DeliveryAgency.LOTTE, "40", "배달전"),
    COMPLETED_LOTTE(DeliveryAgency.LOTTE, "41", "배달완료"),

    //CJ
    PICK_UP(DeliveryAgency.CJ, "11", "집화처리"),
    UNLOAD(DeliveryAgency.CJ, "42", "간선하차"),
    LOAD(DeliveryAgency.CJ, "44", "간선상차"),
    IN_PROGRESS_CJ(DeliveryAgency.CJ, "82", "배달출발"),
    COMPLETED_CJ(DeliveryAgency.CJ, "91", "배달완료"),
}
