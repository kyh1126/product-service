package com.smartfoodnet

import com.smartfoodnet.sample.SampleJavaClass
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SampleApplication

fun main(args: Array<String>) {
	runApplication<SampleApplication>(*args)
	println(SampleJavaClass().sampleHello)
}
