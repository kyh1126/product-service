package com.smartfoodnet.fnproduct.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.smartfoodnet.apiclient.request.BasicProductCreatedModel
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.config.aws.MessageAttribute.APPROXIMATE_FIRST_RECEIVE_TIMESTAMP
import com.smartfoodnet.config.aws.MessageAttribute.MESSAGE_GROUP_ID
import io.awspring.cloud.messaging.listener.Acknowledgment
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ProductListener(
    private val objectMapper: ObjectMapper
) {
    private val messageGroupId = MESSAGE_GROUP_ID.value
    private val firstReceivedTime = APPROXIMATE_FIRST_RECEIVE_TIMESTAMP.value

    @Transactional
    @SqsListener(
        value = ["\${sqs.queues.fn-product.basic-product-created.name}"],
        deletionPolicy = SqsMessageDeletionPolicy.NEVER
    )
    fun basicProductCreated(
        @Payload message: String,
        @Headers headers: Map<String, String>,
        ack: Acknowledgment
    ) {
        val basicProductCreatedModel =
            objectMapper.readValue(message, BasicProductCreatedModel::class.java)
        log.info("$messageGroupId: ${headers[messageGroupId]}, $firstReceivedTime: ${headers[firstReceivedTime]}, message: $message")

        try {
            // TODO: 비동기로 처리하게 되면, 여기서 nosnos 쪽 POST request 하기
            // 메시지 처리 완료
            ack.acknowledge().get()
        } catch (e: Exception) {
            log.error("[ProductListener] basicProductCreated 처리 실패: $message", e)
        }
    }

    companion object : Log
}
