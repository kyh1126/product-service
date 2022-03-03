package com.smartfoodnet.apiclient.response

data class StoreInfoModel(
    val storeId: Long,
    val storeName: String,
    val icon: String,
    val partnerId: Long,
    val enabled: Boolean = false,
)