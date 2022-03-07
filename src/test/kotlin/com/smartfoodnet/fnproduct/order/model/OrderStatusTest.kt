package com.smartfoodnet.fnproduct.order.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class OrderStatusTest{
    @Test
    @DisplayName("상태값 다음처리")
    internal fun enumNext(){
        var status = OrderStatus.NEW.next()
        assertEquals(status, OrderStatus.ORDER_CONFIRM)
        status = status.next()
        assertEquals(status, OrderStatus.RELEASE_REGISTRATION)
    }

    @Test
    @DisplayName("상태값 이전처리")
    internal fun enumPrevious(){
        var status = OrderStatus.NEW
        assertEquals(status.previous(), OrderStatus.NEW)
        status = OrderStatus.CANCEL
        assertEquals(status.previous(), OrderStatus.CANCEL)
    }
}