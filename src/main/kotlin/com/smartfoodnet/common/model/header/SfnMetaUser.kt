package com.smartfoodnet.common.model.header

import java.util.*

class SfnMetaUser(
    val id: Long? = null,
    val email: String? = null,
    var name: String? = null,
    val userType: String? = null, // customer, admin
    val partnerId: Long? = null
) {
    fun decodeName(): SfnMetaUser {
        name = String(Base64.getDecoder().decode(name))
        return this
    }
}
