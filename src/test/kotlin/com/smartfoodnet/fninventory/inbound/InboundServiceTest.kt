package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.apiclient.WmsClient
import com.smartfoodnet.base.AbstractTest
import com.smartfoodnet.common.utils.Log
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@ActiveProfiles("local")
class InboundServiceTest(
    private val inboundService: InboundService,
) {
    @Test
    fun getInboundWorkJob() {
//        inboundService.getInboundWorkJob(77, "20211213")
    }
}
