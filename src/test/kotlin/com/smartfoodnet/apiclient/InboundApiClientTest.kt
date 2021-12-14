package com.smartfoodnet.apiclient

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile

@SpringBootTest
internal class InboundApiClientTest {

    @Autowired
    lateinit var inboundApiClient: InboundApiClient

    @Test
    fun getInbound() {
        val res = inboundApiClient.getInbound(455)
        println(res)
    }
}