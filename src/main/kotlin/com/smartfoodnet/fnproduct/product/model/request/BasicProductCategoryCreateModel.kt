package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.fnproduct.code.entity.Code
import io.swagger.annotations.ApiModelProperty

data class BasicProductCategoryCreateModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @JsonIgnore
    val level1Category: Code? = null,

    @JsonIgnore
    val level2Category: Code? = null,

    @ApiModelProperty(value = "대분류")
    val level1: String? = level1Category?.keyName,

    @ApiModelProperty(value = "중분류")
    val level2: String? = level2Category?.keyName,
)
