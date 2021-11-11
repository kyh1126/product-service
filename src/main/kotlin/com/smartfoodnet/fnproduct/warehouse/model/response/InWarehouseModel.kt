package com.smartfoodnet.fnproduct.warehouse.model.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fnproduct.warehouse.entity.InWarehouse
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class InWarehouseModel(
    @ApiModelProperty(value = "입고처 고유 ID")
    var id: Long? = null,

    @ApiModelProperty(value = "화주(고객사) ID")
    var partnerId: Long?,

    @ApiModelProperty(value = "입고처 이름")
    var name: String?,

    @ApiModelProperty(value = "우편번호")
    var postNumber: String?,

    @ApiModelProperty(value = "주소")
    var address: String?,

    @ApiModelProperty(value = "주소 상세")
    var addressDetail: String?,

    @ApiModelProperty(value = "대표자 이름")
    var representative: String?,

    @ApiModelProperty(value = "사업자 등록번호")
    var businessNumber: String?,

    @ApiModelProperty(value = "연락처")
    var contactNumber: String?,

    @ApiModelProperty(value = "담당자 이름")
    var managerName: String?,

    @ApiModelProperty(value = "담당자 연락처")
    var managerContactNumber: String?,

    @ApiModelProperty(value = "담당자 이메일")
    var managerEmail: String?,

    @ApiModelProperty(value = "생성일자")
    @field:JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    var createdAt: LocalDateTime?,

    @ApiModelProperty(value = "수정일자")
    @field:JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    var updatedAt: LocalDateTime?
) {
    companion object {
        fun fromEntity(inWarehouse: InWarehouse): InWarehouseModel {
            return inWarehouse.run {
                InWarehouseModel(
                    id,
                    partnerId,
                    name,
                    postNumber,
                    address,
                    addressDetail,
                    representative,
                    businessNumber,
                    contactNumber,
                    managerName,
                    managerContactNumber,
                    managerEmail,
                    createdAt,
                    updatedAt
                )
            }
        }
    }
}
