package com.smartfoodnet.fnproduct.warehouse.model.dto

import com.smartfoodnet.fnproduct.product.model.vo.DropType
import com.smartfoodnet.fnproduct.product.model.vo.InspectionType
import com.smartfoodnet.fnproduct.warehouse.entity.OutWarehouse
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel
data class OutWarehouseDto(
    @ApiModelProperty(value = "출고처 이름", example = "출고처")
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

    @ApiModelProperty(value = "하차방식", example = "HANDWORK")
    var dropType: DropType,

    @ApiModelProperty(value = "검수방식", example = "SAMPLE")
    var inspectionType: InspectionType,

    @ApiModelProperty(value = "대기여부", example = "false")
    var waitType: Boolean,

    @ApiModelProperty(value = "담당자 이름", example = "김담당")
    var managerName: String,

    @ApiModelProperty(value = "담당자 연락처", example = "01033334444")
    var managerContactNumber: String,

    @ApiModelProperty(value = "담당자 이메일", example = "test@smartfoodnet.com")
    var managerEmail: String
) {
    fun toEntity(partnerId: Long): OutWarehouse {
        return this.run {
            OutWarehouse(
                partnerId = partnerId,
                name = name,
                postNumber = postNumber,
                address = address,
                addressDetail = addressDetail,
                representative = representative,
                businessNumber = businessNumber,
                contactNumber = contactNumber,
                dropType = dropType,
                inspectionType = inspectionType,
                waitType = waitType,
                managerName = managerName,
                managerContactNumber = managerContactNumber,
                managerEmail = managerEmail
            )
        }
    }
}
