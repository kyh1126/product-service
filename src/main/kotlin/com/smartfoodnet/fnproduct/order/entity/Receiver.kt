package com.smartfoodnet.fnproduct.order.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class Receiver (
    @Column(name = "receiver_name")
    val name: String,
    @Column(name = "zip_code")
    val zipCode: String? = null,
    @Column(name = "receiver_address")
    val address: String,
    @Column(name = "receiver_phone_number")
    val phoneNumber: String,
)