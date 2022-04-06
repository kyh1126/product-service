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
    PARCEL_COLLECTION(DeliveryAgency.LOTTE, "10", "출력"),
    SHIPPING_CODE_REGISTERED(DeliveryAgency.LOTTE, "12", "집하완료"),
    SHIPPING_IN_PROGRESS(DeliveryAgency.LOTTE, "20", "발송"),
    ARRIVAL(DeliveryAgency.LOTTE, "21", "도착"),
    BEFORE_SHIPPING(DeliveryAgency.LOTTE, "40", "배달전"),
    SHIPPING_COMPLETED(DeliveryAgency.LOTTE, "41", "배달완료")
}
