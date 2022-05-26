package com.smartfoodnet.fnproduct.store.model.response

import com.smartfoodnet.fnproduct.product.model.response.BasicProductSimpleModel
import java.time.LocalDateTime

data class StoreProductFlatModel (
    var id: Long? = null,
    var storeId: Long,
    var storeName: String,
    var storeIcon: String? = null,
    var partnerId: Long,
    var name: String,
    var storeProductCode: String? = null,
    var optionName: String? = null,
    var optionCode: String? = null,
    val basicProduct: BasicProductSimpleModel,
    val quantity: Int?,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null
)
