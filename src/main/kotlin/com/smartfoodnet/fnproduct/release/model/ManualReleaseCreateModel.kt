package com.smartfoodnet.fnproduct.release.model

import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.CollectedProductInfo
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.order.entity.Receiver
import com.smartfoodnet.fnproduct.order.vo.DeliveryType
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.order.vo.MatchingType
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.order.vo.StoreSyncStatus
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.store.enums.ReservedStoreCode
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.absoluteValue

data class ManualReleaseCreateModel(
    @ApiModelProperty(value = "받는이 이름", example = "홍길동")
    val receiverName: String,
    @ApiModelProperty(value = "받는이 전화번호", example = "010-1234-5678")
    val receiverPhoneNumber: String,
    @ApiModelProperty(value = "받는이 주소", example = "서울 강남구 남부순환로 2753 3층 스마트푸드네트웍스")
    val receiverAddress: String,
    @ApiModelProperty(value = "우편번호", example = "")
    val receiverZipCode: String? = null,
    @ApiModelProperty(value = "배송 타입", example = "PARCEL(택배), VEHICLE(차량), DAWN(새벽배송), SAME_DAY(당일배송)")
    val deliveryType: DeliveryType = DeliveryType.PARCEL,
    @ApiModelProperty(value = "행사여부", example = "Y, N, 행사메모입력")
    val promotion: String?,
    @ApiModelProperty(value = "재출고사유", example = "배송 된 상품의 유통기간이 지났다")
    val reShipmentReason: String?,
    @ApiModelProperty(value = "출고상품", example = "[{productId, quantity}, ...]")
    var products: List<ManualReleaseProductInfo>
) {


    fun toCollectOrderEntity(
        partnerId: Long,
        orderUniqueKey: String
    ): CollectedOrder {
        val now = LocalDateTime.now()

        return CollectedOrder(
            partnerId = partnerId,
            orderUniqueKey = orderUniqueKey,
            bundleNumber = generateBundleNumber(
                partnerId = partnerId,
                date = now,
                orderNumber = orderUniqueKey
            ),
            storeName = "",
            storeCode = ReservedStoreCode.NON_ORDER.storeCode,
            storeId = ReservedStoreCode.NON_ORDER.storeId,
            collectedProductInfo = CollectedProductInfo.empty(),
            userStoreId = null,
            orderedAt = null,
            collectedAt = null,
            statusUpdatedAt = null,
            orderNumber = orderUniqueKey,
            status = OrderStatus.NEW,
            storeSyncStatus = StoreSyncStatus.NONE,
            claimStatus = null,
            deliveryType = deliveryType,
            expectedDeliveryDate = null,
            price = null,
            shippingPrice = null,
            quantity = 1,
            receiver = Receiver(
                name = receiverName,
                phoneNumber = receiverPhoneNumber,
                address = receiverAddress,
                zipCode = receiverZipCode
            ),
            uploadType = OrderUploadType.MANUAL
        )
    }

    companion object {
        /**
         * 유니크한 10자리 문자열(UUID)
         */
        fun generateOrderUniqueKey(): String {
            return String.format("%010d", UUID.randomUUID().hashCode().absoluteValue)
        }

        /**
         * 주문외 출고 묶음 번호 채번 규칙
         * - partner id, length 4
         * - registered date, length 6
         * - store code, length 4, 9999
         * - unique value, length 10
         */
        fun generateBundleNumber(
            partnerId: Long,
            date: LocalDateTime,
            orderNumber: String,
        ): String {
            if (orderNumber.length != 10) throw IllegalArgumentException("유니크한 주문번호가 아닙니다.")

            val partnerIdSegment = String.format("%04d", partnerId)
            val dateSegment = date.format(DateTimeFormatter.ofPattern("yyMMdd"))
            val storeCodeSegment = ReservedStoreCode.NON_ORDER.storeCode?.padStart(4, '0')
            val orderNumberSegment = orderNumber.padStart(10, '0')

            return "$partnerIdSegment$dateSegment$storeCodeSegment$orderNumberSegment"
        }
    }
}

data class ManualReleaseProductInfo(
    var productId: Long,
    var quantity: Int,
) {
    fun toConfirmProduct(
        collectedOrder: CollectedOrder,
        basicProduct: BasicProduct
    ): ConfirmProduct {
        return ConfirmProduct(
            collectedOrder = collectedOrder,
            type = basicProduct.type,
            matchingType = MatchingType.MANUAL,
            basicProduct = basicProduct,
            quantity = quantity,
            quantityPerUnit = quantity
        )
    }
}