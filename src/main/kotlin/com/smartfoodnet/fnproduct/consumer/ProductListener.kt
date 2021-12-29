package com.smartfoodnet.fnproduct.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.config.aws.MessageAttribute.APPROXIMATE_FIRST_RECEIVE_TIMESTAMP
import com.smartfoodnet.config.aws.MessageAttribute.MESSAGE_GROUP_ID
import io.awspring.cloud.messaging.listener.Acknowledgment
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy
import io.awspring.cloud.messaging.listener.annotation.SqsListener
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ProductListener(
    private val objectMapper: ObjectMapper
) {
    private val messageGroupId = MESSAGE_GROUP_ID.value
    private val firstReceivedTime = APPROXIMATE_FIRST_RECEIVE_TIMESTAMP.value

    @Transactional
//    @SqsListener(
//        value = ["\${sqs.queues.fn-product.basic-product-created.name}"],
//        deletionPolicy = SqsMessageDeletionPolicy.NEVER
//    )
    fun receiveMessage(
        @Payload message: String,
        @Headers headers: Map<String, String>,
        ack: Acknowledgment
    ) {
        objectMapper.readValue(message, Map::class.java)
            .also {
                log.info("$messageGroupId: ${headers[messageGroupId]}, $firstReceivedTime: ${headers[firstReceivedTime]}, message: $message")

                // TODO: RestTemplate 로 nosnos 쪽 POST request 하기

            }
    }

    companion object : Log
}
