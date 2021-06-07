package com.smartfoodnet.sample.hello

import com.smartfoodnet.sample.hello.entity.Demo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DemoService {
    @Autowired
    lateinit var demoRepository: DemoRepository

    @Autowired
    lateinit var qDemoRepository: QDemoRepository

    fun getDemos(): List<Demo> {
        return demoRepository.findAll()
    }

    fun getDemosByQuerydsl(): List<Demo> {
        return qDemoRepository.getDemos()
    }
}