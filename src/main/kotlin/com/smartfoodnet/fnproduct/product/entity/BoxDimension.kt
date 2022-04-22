package com.smartfoodnet.fnproduct.product.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class BoxDimension(
    @Column(name = "box_width")
    var width: Int? = null,

    @Column(name = "box_length")
    var length: Int? = null,

    @Column(name = "box_height")
    var height: Int? = null,

    @Column(name = "box_weight")
    var weight: Int? = null
)
