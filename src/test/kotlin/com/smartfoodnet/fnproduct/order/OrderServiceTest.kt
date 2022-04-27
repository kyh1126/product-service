package com.smartfoodnet.fnproduct.order

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local")
internal class OrderServiceTest{

    @Autowired
    lateinit var orderService: OrderService

    @Test
    @DisplayName("결품영향 주문 조회")
    fun missingAffectedOrders (){
        orderService.getMissingAffectedOrder(11, 36)
    }
}