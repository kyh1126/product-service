package com.smartfoodnet.fnproduct.order.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class Receiver (
    @Column(name = "receiver_name")
    var name: String? = null,
    @Column(name = "receiver_address")
    var address: String? = null,
    @Column(name = "receiver_phone_number")
    var phoneNumber: String? = null,
)