package com.smartfoodnet.apiclient.response

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class GetInboundWorkModelTest{
    @Test
    @DisplayName("가져온 모델의 유니크 ID 변환")
    fun getModelUniquId(){
        //given
        val model = GetInboundWorkModel(
            work_date = LocalDateTime.now(),
            quantity = 10,
            receiving_type = 1,
            shipping_product_id = 1234,
            work_type = 1
        )

        val expected : String;
        with(model) {
            expected = "${work_date.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}_${work_type}_${receiving_type}_${shipping_product_id}_${quantity}"
        }
        println(expected)
        assertEquals(expected, model.uniqueId)
    }
}