package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.apiclient.WmsFeignClient
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fninventory.stock.StockService
import com.smartfoodnet.fnproduct.product.model.request.BasicProductSearchCondition
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
@ActiveProfiles("local")
@SpringBootTest
class StockServiceTest(
)
//    : AbstractTest()
{
    @Autowired
    lateinit var wmsFeignClient: WmsFeignClient

    @Autowired
    lateinit var stockService: StockService


    @Test
    fun feignClientTest() {
        println(wmsFeignClient.getStocks(42L, listOf(3508,3506)))
    }

    @Test
    fun getStocksFeignTest() {
        val stocks = stockService.getBasicProductStocks(partnerId = 77, BasicProductSearchCondition(partnerId = 77), PageRequest.of(1, 50))
        println(stocks)
    }

    companion object : Log
}
