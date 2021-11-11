package com.smartfoodnet.fnproduct.warehouse.model.dto

import com.smartfoodnet.fnproduct.warehouse.entity.InWarehouse
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel
data class InWarehouseDto(
    @ApiModelProperty(value = "입고처 이름", example = "입고처")
    var name: String,

    @ApiModelProperty(value = "우편번호", example = "01612")
    var postNumber: String,

    @ApiModelProperty(value = "주소", example = "서울특별시")
    var address: String,

    @ApiModelProperty(value = "주소 상세", example = "동대문구 123")
    var addressDetail: String,

    @ApiModelProperty(value = "대표자 이름", example = "대표자")
    var representative: String,

    @ApiModelProperty(value = "사업자 등록번호", example = "1234567890")
    var businessNumber: String,

    @ApiModelProperty(value = "연락처", example = "01011112222")
    var contactNumber: String,

    @ApiModelProperty(value = "담당자 이름", example = "김담당")
    var managerName: String,

    @ApiModelProperty(value = "담당자 연락처", example = "01033334444")
    var managerContactNumber: String,

    @ApiModelProperty(value = "담당자 이메일", example = "test@smartfoodnet.com")
    var managerEmail: String
) {
    fun toEntity(partnerId: Long): InWarehouse {
        return this.run {
            InWarehouse(
                partnerId = partnerId,
                name = name,
                postNumber = postNumber,
                address = address,
                addressDetail = addressDetail,
                representative = representative,
                businessNumber = businessNumber,
                contactNumber = contactNumber,
                managerName = managerName,
                managerContactNumber = managerContactNumber,
                managerEmail = managerEmail
            )
        }
    }
}
