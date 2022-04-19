package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.request.TrackingNumberRegisterModel
import com.smartfoodnet.base.partnerId
import com.smartfoodnet.fnproduct.release.model.vo.DeliveryAgency
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class OrderNumberSplitTest {
    @Test
    @DisplayName("같은 주문번호인지(상품주문번호 제외) 체크 성공한다")
    fun checkSameOrderNumbers_NAVER() {
        val orderNumbers = getSameOrderNumbers_NAVER()

        val processedOrderNumbers_NAVER = orderNumbers
            .map { it.split(' ').first() }.distinct()

        assertTrue(processedOrderNumbers_NAVER.size == 1)
    }

    @Test
    @DisplayName("같은 주문번호인(상품주문번호 제외) TrackingNumberRegisterModel distinct 성공한다")
    fun checkDistinctModel_NAVER() {
        val orderNumbers = getSameOrderNumbers_NAVER()

        val processedModels_NAVER = orderNumbers
            .map {
                TrackingNumberRegisterModel(
                    partnerId = partnerId,
                    storeCode = "NAVER",
                    orderNumber = it.split(' ').first(),
                    receiverName = "받는이",
                    deliveryAgency = DeliveryAgency.CJ,
                    trackingNumber = it.split(' ').last() // 다른 숫자로 표시하기 위하여..
                )
            }.distinctBy { it.orderNumber }

        assertTrue(processedModels_NAVER.size == 1)
    }

    private fun getSameOrderNumbers_NAVER() =
        listOf(
            "2022021459758121 2022021425450591",
            "2022021459758121 2022021425450581"
        )

    private val differentOrderNumber_NAVER = "2022032939781291 2022032962552532"

}
