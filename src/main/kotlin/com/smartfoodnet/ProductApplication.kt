package com.smartfoodnet

import com.smartfoodnet.common.model.response.CommonResponse
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@EnableFeignClients
@SpringBootApplication
class ProductApplication(){

    @RestController
    @RequestMapping("/health")
    class HealthCheckController {
        @GetMapping
        fun healthCheck(): CommonResponse<String> {
            return CommonResponse("fn-product ok ok wow!!!")
        }
    }
}

fun main(args: Array<String>) {
    runApplication<ProductApplication>(*args)
}
