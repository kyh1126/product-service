package com.smartfoodnet.fnproduct.order.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class Sender (
    @Column(name = "sender_name")
    var name: String? = null,
    @Column(name = "sender_address")
    var address: String? = null,
    @Column(name = "sender_phone_number")
    var phoneNumber: String? = null,
)