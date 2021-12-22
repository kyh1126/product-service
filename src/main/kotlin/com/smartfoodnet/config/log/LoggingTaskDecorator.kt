package com.smartfoodnet.config.log

import org.slf4j.MDC
import org.springframework.core.task.TaskDecorator

/**
 * 각 비동기 쓰레드마다 로그를 남길 경우 자신을 호출한 쓰레드를 알 수 있어, 디버그시 추적이 용이해진다.
 */
class LoggingTaskDecorator : TaskDecorator {

    override fun decorate(task: Runnable): Runnable {
        val callerThreadContext = MDC.getCopyOfContextMap()

        return Runnable {
            callerThreadContext?.let {
                MDC.setContextMap(it)
            }
            task.run()
        }
    }
}
