package com.smartfoodnet.fnproduct.order.entity

import javax.persistence.Embeddable

@Embeddable
class Memo(
    var memo1 : String? = null,
    var memo2 : String? = null,
    var memo3 : String? = null,
    var memo4 : String? = null,
    var memo5 : String? = null
)