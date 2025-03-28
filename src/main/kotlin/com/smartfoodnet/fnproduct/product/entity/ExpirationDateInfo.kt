package com.smartfoodnet.fnproduct.product.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class ExpirationDateInfo(
    @Column(name = "manufacture_date_write_yn")
    var manufactureDateWriteYn: String = "N",

    @Column(name = "expiration_date_write_yn")
    var expirationDateWriteYn: String = "N",

    @Column(name = "manufacture_to_expiration_date")
    var manufactureToExpirationDate: Long? = null,
) {
    companion object {
        val default = ExpirationDateInfo()
    }
}
