package com.smartfoodnet.sample

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local")
class CodeTest {

    @Autowired
    lateinit var a: Sample

    @Test
    fun test() {
        println("Before, in test: " + Thread.currentThread())
        for(i in 0 .. 10) {
            a.async()
        }
        println("After, in test: " + Thread.currentThread())
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