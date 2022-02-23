package com.smartfoodnet.fnproduct.order.entity

import javax.persistence.Embeddable

@Embeddable
class CollectedProductInfo(
    var collectedStoreProductCode : String? = null,
    var collectedStoreProductName : String? = null,
    var collectedStoreProductOptionName: String? = null,
    var collectedStoreProductOptionCode: String? = null
)