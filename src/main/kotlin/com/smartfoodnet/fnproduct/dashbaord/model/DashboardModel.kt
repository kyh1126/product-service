package com.smartfoodnet.fnproduct.dashbaord.model

class DashboardModel(
    val collectedOrder : CollectedOrderCount
)

data class CollectedOrderCount(
    val collectedCount : Long = 0,
    val unprocessedCount : Long = 0,
    val canceledCount : Long = 0
)