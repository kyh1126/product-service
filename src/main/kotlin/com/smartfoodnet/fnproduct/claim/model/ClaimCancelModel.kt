package com.smartfoodnet.fnproduct.claim.model

import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotNull

data class ClaimCancelModel(
    @NotNull
    @ApiModelProperty(value = "화주(고객사) ID", example = "14")
    val partnerId: Long,
    @NotNull
    @ApiModelProperty(value = "클레임 ID", example = "14")
    val claimId: Long,
)
