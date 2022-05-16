package com.smartfoodnet.fninventory.stock.model

data class AvailableStockModel(
    val basicProductId : Long,
    var stock : Int = 0
)