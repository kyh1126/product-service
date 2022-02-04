package com.smartfoodnet.sample

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles

class CodeTest {
    @Test
    fun test() {
        for (i in 1 downTo 1) {
            println("hi")
        }
    }
}

@Component
class Sample {
    @Async("taskExecutor")
    fun async() {
            println("In async: " + Thread.currentThread())
            Thread.sleep(3000)
    }
}