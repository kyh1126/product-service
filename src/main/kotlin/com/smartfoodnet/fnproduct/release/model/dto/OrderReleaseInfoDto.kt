package com.smartfoodnet.fnproduct.release.model.dto

import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo

data class OrderReleaseInfoDto(
    val confirmOrder: ConfirmOrder,
    val releaseInfoList: List<ReleaseInfo>
)
