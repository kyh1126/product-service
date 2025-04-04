package com.smartfoodnet.fnproduct.warehouse.model.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(value = "화주 입고처 업데이트 모델")
data class InWarehouseUpdateDto(
    @ApiModelProperty(value = "입고처 이름", example = "입고처")
    var name: String? = null,

    @ApiModelProperty(value = "우편번호", example = "01612")
    var postNumber: String? = null,

    @ApiModelProperty(value = "주소", example = "서울특별시")
    var address: String? = null,

    @ApiModelProperty(value = "주소 상세", example = "동대문구 123")
    var addressDetail: String? = null,

    @ApiModelProperty(value = "대표자 이름", example = "대표자")
    var representative: String? = null,

    @ApiModelProperty(value = "사업자 등록번호", example = "1234567890")
    var businessNumber: String? = null,

    @ApiModelProperty(value = "연락처", example = "01011112222")
    var contactNumber: String? = null,

    @ApiModelProperty(value = "담당자 이름", example = "김담당")
    var managerName: String? = null,

    @ApiModelProperty(value = "담당자 연락처", example = "01033334444")
    var managerContactNumber: String? = null,

    @ApiModelProperty(value = "담당자 이메일", example = "test@smartfoodnet.com")
    var managerEmail: String? = null,
)
