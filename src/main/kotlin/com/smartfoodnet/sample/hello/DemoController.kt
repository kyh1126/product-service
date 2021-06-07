package com.smartfoodnet.sample.hello

import com.smartfoodnet.sample.hello.entity.Demo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/demo")
class DemoController {
    @Autowired
    lateinit var demoService: DemoService

    @GetMapping("")
    fun getDemos(): List<Demo> {
        return demoService.getDemos()
    }

    @GetMapping("/querydsl")
    fun getDemosByQueryDsl(): List<Demo> {
        return demoService.getDemosByQuerydsl()
    }
}