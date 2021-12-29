package com.smartfoodnet.sample

import com.smartfoodnet.apiclient.StockApiClient
import org.assertj.core.util.Lists
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestApplication {

    @Autowired
    lateinit var stockApiClient: StockApiClient;

    @Test
    fun wmsStockTest() {
        val shippingProductIds = Lists.newArrayList<Long>();
        shippingProductIds.add(4947)
        shippingProductIds.add(4948)
        val stocks = stockApiClient.getStocks(77, shippingProductIds)
        println(stocks)
    }
}
