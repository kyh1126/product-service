package com.smartfoodnet.config

import com.smartfoodnet.config.log.LoggingTaskDecorator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@EnableAsync
class AsyncConfig(
    @Value("\${sfn.async.poolSize:10}")
    private val poolSize: Int,
) {
    @Bean
    fun taskExecutor(): ThreadPoolTaskExecutor {
        val taskExecutor = ThreadPoolTaskExecutor()

        taskExecutor.setThreadNamePrefix("QueueTask-")
        taskExecutor.corePoolSize = poolSize
        taskExecutor.maxPoolSize = poolSize * 2
        taskExecutor.setQueueCapacity(poolSize * 5)
        taskExecutor.setTaskDecorator(LoggingTaskDecorator())
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true)

        return taskExecutor
    }

    @Bean
    fun shortageTaskExecutor(): ThreadPoolTaskExecutor {
        val taskExecutor = ThreadPoolTaskExecutor()

        taskExecutor.setThreadNamePrefix("ShortageTask-")
        taskExecutor.corePoolSize = poolSize
        taskExecutor.maxPoolSize = poolSize * 2
        taskExecutor.setQueueCapacity(poolSize * 5)
        taskExecutor.setTaskDecorator(LoggingTaskDecorator())
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true)

        return taskExecutor
    }
}
