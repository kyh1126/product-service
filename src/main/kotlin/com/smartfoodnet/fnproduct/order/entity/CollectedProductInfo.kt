package com.smartfoodnet.fnproduct.order.entity

import javax.persistence.Embeddable

@Embeddable
class CollectedProductInfo(
    var collectedStoreProductCode : String,
    var collectedStoreProductName : String,
    var collectedStoreProductOptionName: String? = null,
    var collectedStoreProductOptionCode: String? = null
)