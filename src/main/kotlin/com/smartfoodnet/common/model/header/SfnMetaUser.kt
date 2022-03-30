package com.smartfoodnet.common.model.header

class SfnMetaUser(
        val id: Long? = null,
        val email: String? = null,
        val name: String? = null,
        val userType: String? = null, // customer, admin
        val partnerId: Long? = null
)
