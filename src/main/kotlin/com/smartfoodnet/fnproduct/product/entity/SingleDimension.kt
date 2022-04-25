package com.smartfoodnet.fnproduct.product.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class SingleDimension(
    @Column(name = "single_width")
    var width: Int,

    @Column(name = "single_length")
    var length: Int,

    @Column(name = "single_height")
    var height: Int,

    @Column(name = "single_weight")
    var weight: Int? = null
) {
    companion object {
        val default = SingleDimension(width = 0, length = 0, height = 0)
    }
}
