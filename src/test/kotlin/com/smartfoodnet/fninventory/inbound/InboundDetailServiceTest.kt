package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.entity.InboundDetail
import com.smartfoodnet.fninventory.inbound.model.request.InboundCreateModel
import com.smartfoodnet.fninventory.inbound.model.vo.InboundMethodType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("dev")
class InboundDetailServiceTest @Autowired constructor(
    val inboundService: InboundService,
    val inboundDetailService: InboundDetailService,
) {

//    @BeforeEach
//    fun setUp() {
//        val inbound = InboundCreateModel(
//            partnerId = 1L,
//            basicProductCode = "0001AB00003",
//            inboundRequestQuantity = 100L,
//            inboundExpectedDate = LocalDateTime.now(),
//            inboundMethod = InboundMethodType.DELIVERY
//        )
//
//        inboundService.createInbound(listOf(inbound))
//        val inboundDetail = InboundDetail(
//            inbound = inbound.toEntity(),
//            actualQuantity = 70L,
//            boxQuantity = 30L,
//            palletQuantity = 1L,
//            manufactureDate = null,
//            expirationDate = LocalDateTime.now(),
//            actualInboundDate = LocalDateTime.now()
//        )
//    }

//    @Test
//    @DisplayName("Repository test")
//    fun testInboundDetailRepositoryTest() {
//        inboundDetailService.getDetail()
//    }
}