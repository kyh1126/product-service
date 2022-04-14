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
