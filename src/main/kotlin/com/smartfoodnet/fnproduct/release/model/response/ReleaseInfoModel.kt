package com.smartfoodnet.fnproduct.release.model.response

import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import io.swagger.annotations.ApiModelProperty
import org.apache.logging.log4j.util.Strings
import java.time.LocalDateTime

data class ReleaseInfoModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "NOSNOS 발주 id")
    var orderId: Long? = null,

    @ApiModelProperty(value = "출고코드")
    var orderCode: String? = null,

    @ApiModelProperty(value = "NOSNOS 출고 id")
    var releaseId: Long? = null,

    @ApiModelProperty(value = "릴리즈코드")
    var releaseCode: String? = null,

    @ApiModelProperty(value = "NOSNOS 출고진행상태")
    var releaseStatus: ReleaseStatus? = null,

    @ApiModelProperty(value = "송장번호")
    var shippingCode: String? = null,

    @ApiModelProperty(value = "택배사 id")
    var deliveryAgencyId: Long? = null,

    @ApiModelProperty(value = "택배사명")
    var deliveryAgencyName: String? = null,

    @ApiModelProperty(value = "송장번호부여일시")
    var shippingCodeCreatedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "매칭상품명")
    var productNames: String? = null,

    @ApiModelProperty(value = "매칭상품코드")
    var productCodes: String? = null,

    @ApiModelProperty(value = "주문수량(매칭상품기준)")
    var quantities: String? = null,

    @ApiModelProperty(value = "받는분이름")
    var receiverName: String? = null,

    @ApiModelProperty(value = "받는분주소")
    var receiverAddress: String? = null,

    @ApiModelProperty(value = "업로드방식")
    var uploadType: OrderUploadType? = null,

    @ApiModelProperty(value = "쇼핑몰 이름")
    var storeName: String? = null,

    @ApiModelProperty(value = "묶음번호")
    var bundleNumber: String? = null,

    @ApiModelProperty(value = "주문번호")
    var orderNumbers: String? = null,

    @ApiModelProperty(value = "주문상태")
    var orderStatuses: String? = null,

    @ApiModelProperty(value = "클레임상태")
    var claimStatuses: String? = null,
) {
    companion object {
        fun fromEntity(releaseInfo: ReleaseInfo): ReleaseInfoModel {
            val collectedOrders = releaseInfo.releaseOrderMappings.map { it.collectedOrder }
            return releaseInfo.run {
                ReleaseInfoModel(
                    id = id,
                    orderId = orderId,
                    orderCode = orderCode,
                    releaseId = releaseId,
                    releaseCode = releaseCode,
                    releaseStatus = releaseStatus,
                    shippingCode = shippingCode,
                    deliveryAgencyId = deliveryAgencyId,
                    shippingCodeCreatedAt = shippingCodeCreatedAt,
                    productNames = collectedOrders.joinToString {
                        Strings.concat(it.storeProduct?.name, it.storeProduct?.optionName)
                            ?: Strings.concat(
                                it.collectedProductInfo.collectedStoreProductName,
                                it.collectedProductInfo.collectedStoreProductOptionName
                            )
                    },
                    productCodes = collectedOrders.map { collectedOrder ->
                        if (collectedOrder.uploadType == OrderUploadType.MANUAL) {
                            // 주문외출고
                            collectedOrder.confirmProductList.map { confirmProduct ->
                                confirmProduct.basicProduct.productCode
                            }
                        } else {
                            // 일반 쇼핑몰 주문
                            mutableListOf(collectedOrder.collectedProductInfo.collectedStoreProductCode)
                        }
                    }.flatten().joinToString(),
                    quantities = collectedOrders.joinToString { (it.quantity ?: 0).toString() },
                    receiverName = collectedOrders.firstOrNull()?.receiver?.name,
                    receiverAddress = collectedOrders.firstOrNull()?.receiver?.address,
                    uploadType = collectedOrders.firstOrNull()?.uploadType,
                    storeName = collectedOrders.firstOrNull()?.storeName,
                    bundleNumber = collectedOrders.firstOrNull()?.bundleNumber,
                    orderNumbers = collectedOrders.joinToString { it.orderNumber ?: "" },
                    orderStatuses = collectedOrders.joinToString { it.status.name },
                    claimStatuses = collectedOrders.joinToString { it.claimStatus ?: "" }
                )
            }
        }
    }
}
