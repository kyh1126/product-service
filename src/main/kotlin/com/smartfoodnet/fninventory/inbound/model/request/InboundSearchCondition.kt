package com.smartfoodnet.fninventory.inbound.model.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import com.smartfoodnet.fninventory.inbound.model.vo.ProductSearchType
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

class InboundSearchCondition(
    @ApiModelProperty(value = "고객사 ID", example = "1", hidden = true)
    @JsonIgnore
    var partnerId: Long? = null,

    @ApiModelProperty(value = "검색 시작 일자", example = "2021-12-01 00:00:00")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    var fromDate: LocalDateTime,

    @ApiModelProperty(value = "검색 종료 일자", example = "2021-12-07 23:59:59")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    var toDate: LocalDateTime,

    @ApiModelProperty(value = "입고상태", example = "REQUEST")
    var statusType: InboundStatusType? = null,

    @ApiModelProperty(value = "상품별검색")
    var productSearchType: ProductSearchType? = null,

    @ApiModelProperty(value = "상품별검색")
    var keyword: String? = null
)