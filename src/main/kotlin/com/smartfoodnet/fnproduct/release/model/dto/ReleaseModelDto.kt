package com.smartfoodnet.fnproduct.release.model.dto

import com.smartfoodnet.apiclient.response.NosnosReleaseItemModel
import com.smartfoodnet.apiclient.response.NosnosReleaseModel
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder

data class ReleaseModelDto(
    val releaseModel: NosnosReleaseModel,
    val releaseItemModels: List<NosnosReleaseItemModel>,
    val confirmOrder: ConfirmOrder
)
