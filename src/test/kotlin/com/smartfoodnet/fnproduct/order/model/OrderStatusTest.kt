package com.smartfoodnet.fnproduct.order.model

import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class OrderStatusTest{
    @Test
    @DisplayName("상태값 다음처리")
    internal fun enumNext(){
        var status = OrderStatus.NEW.next()
        assertEquals(status, OrderStatus.ORDER_CONFIRM)
        status = status.next()
        assertEquals(status, OrderStatus.BEFORE_RELEASE_REQUEST)
    }
}