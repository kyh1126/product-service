package com.smartfoodnet.fnproduct.product.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class SingleDimension(
    @Column(name = "single_width")
    var width: Int? = null,

    @Column(name = "single_length")
    var length: Int? = null,

    @Column(name = "single_height")
    var height: Int? = null,

    @Column(name = "single_weight")
    var weight: Int? = null
)
