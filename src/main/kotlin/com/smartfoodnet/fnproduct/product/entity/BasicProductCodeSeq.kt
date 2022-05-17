package com.smartfoodnet.fnproduct.product.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "basic_product_code_seq")
class BasicProductCodeSeq(
    @Id
    @Column(name = "partner_id", nullable = false)
    val partnerId: Long = 0,

    @Column(name = "seq", nullable = false)
    var seq: Int = 1
) {
    companion object {
        fun initial(partnerId: Long): BasicProductCodeSeq = BasicProductCodeSeq(partnerId)
    }
}
