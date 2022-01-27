package com.smartfoodnet.config.async

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class SpringAsyncConfig {
    @Bean
    fun threadPoolTaskExecutor(): Executor? {
        val taskExecutor = ThreadPoolTaskExecutor()
        taskExecutor.corePoolSize = 3
        taskExecutor.maxPoolSize = 100
        taskExecutor.setQueueCapacity(10)
        taskExecutor.setThreadNamePrefix("Executor-")
        taskExecutor.initialize()
        return taskExecutor
    }
}