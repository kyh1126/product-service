package com.smartfoodnet.apiclient

import com.fasterxml.jackson.core.JsonProcessingException
import com.smartfoodnet.apiclient.SfnService.FN_PRODUCT
import com.smartfoodnet.apiclient.model.SfnMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
//@Async("taskExecutor") // 비동기로 메시지 send 할 때 설정
class MessageApiClient(
    @Value("\${sfn.service.fn-message}") override val host: String,
    override val restTemplate: RestTemplate,
) : RestTemplateClient(host, restTemplate) {

    fun sendMessage(destination: SfnTopic, subject: String? = null, message: Any): String? {
        return post(
            "/messages/sns?destination=$destination",
            SfnMessage(subject, jsonProcessedBody(message))
        )
    }

    fun sendMessage(destination: SfnQueue, subject: String? = null, message: Any): String? {
        return post(
            "/messages/sqs?destination=$destination",
            SfnMessage(subject, jsonProcessedBody(message))
        )
    }

    private fun jsonProcessedBody(message: Any): String {
        try {
            return objectMapper.writeValueAsString(message)
        } catch (e: JsonProcessingException) {
            log.error("[MessageApiClient] 잘못된 메시지 형식입니다. (message: $message)", e)
            throw IllegalArgumentException("Format $message is illegal")
        }
    }
}

enum class SfnService {
    FN_PRODUCT
}

enum class SfnQueue(val service: SfnService) {
    BASIC_PRODUCT_CREATED(FN_PRODUCT)
}

enum class SfnTopic {
    PRODUCT_CREATED
}
