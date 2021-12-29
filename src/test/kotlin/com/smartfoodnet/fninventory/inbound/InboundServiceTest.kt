package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.base.AbstractTest
import com.smartfoodnet.common.utils.Log
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.test.context.TestConstructor

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
class InboundServiceTest(
    private val inboundService: InboundService
) : AbstractTest() {

//    @MockBean
//    lateinit var basicProductRepository : BasicProductRepository
//
//    @Test
//    @DisplayName("입고등록을 성공한다")
//    @Order(1)
//    fun createInbound() {
//        val inbound = InboundCreateModel(
//            partnerId = null,
//            basicProductCode = "0001AB00003",
//            inboundRequestQuantity = 100L,
//            inboundMethod = InboundMethodType.DELIVERY,
//            inboundExpectedDate = LocalDateTime.now().plusDays(3),
//            trackingNo = null,
//            deliveryName = null
//        )
//
//        inboundService.createInbound(listOf(inbound))
//    }

//    @Test
//    @DisplayName("입고관리 단일 조회")
//    @Order(2)
//    fun testGetInbound() {
//        // given
//        val inboundId = 1L
//        // when
//        val inbound = inboundService.getInbound(inboundId)
//        // then
//        assertEquals(inboundId, inbound.id)
//        assertNotNull(inbound.basicProduct)
//        assertEquals(inbound.inboundMethod, InboundMethodType.DELIVERY)
//    }

    //    @Test
//    @DisplayName("입고등록 및 현황 - 검색 조건 포함 조회")
//    fun testGetInboundIncludeCondition() {
//        val formatter = DateTimeFormatter.ofPattern(Constants.TIMESTAMP_FORMAT)
//        val condition = InboundSearchCondition(
//            1L,
//            RequestDate(LocalDateTime.parse("2021-12-01 04:22:33", formatter), LocalDateTime.now()),
//            InboundStatusType.REQUEST,
//            ProductSearchType.BASIC_PRODUCT_NAME,
//            null
//        )
//        println(inboundService.getInbounds(1L, condition, page))
//    }
    companion object : Log
}
