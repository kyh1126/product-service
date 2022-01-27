package com.smartfoodnet.sample

import com.smartfoodnet.fninventory.stock.StockScheduledService
import com.smartfoodnet.fninventory.stock.support.DailyStockSummaryRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.scheduling.annotation.Async
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@ActiveProfiles("local")
@RunWith(value = SpringRunner::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CodeTest {
    @Autowired
    lateinit var stockScheduledService: StockScheduledService

    @Autowired
    lateinit var dailyStockSummaryRepository: DailyStockSummaryRepository

    @Test
    fun test() {
       stockScheduledService.deleteDuplicateSummaries(100)
//        val allSummaries = dailyStockSummaryRepository.findAll()
//        val summaryByEffectiveDate = dailyStockSummaryRepository.findAllByEffectiveDate()
    }


    @Async("threadPoolTaskExecutor")
    fun asyncFun() {
        threadTest()
    }

    fun threadFun() {
        Thread() {
            threadTest()
        }.start()
    }

    fun threadTest() {
        println("hi")
        Thread.sleep(3000)
    }
}