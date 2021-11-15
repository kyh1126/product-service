package com.smartfoodnet.fnproduct.product.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class ExpirationDateInfo(
    @Column(name = "manufacture_date_write_yn")
    var manufactureDateWriteYn: String = "N",

    @Column(name = "expiration_date_write_yn")
    var expirationDateWriteYn: String = "N",

    @Column(name = "expiration_date")
    var expirationDate: Int? = null,
)
