package com.smartfoodnet.fnproduct.product.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class BoxDimension(
    @Column(name = "box_width")
    var width: Int,

    @Column(name = "box_length")
    var length: Int,

    @Column(name = "box_height")
    var height: Int,

    @Column(name = "box_weight")
    var weight: Int? = null
) {
    companion object {
        val default = BoxDimension(width = 0, length = 0, height = 0)
    }
}
