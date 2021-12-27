package com.smartfoodnet.config.aws

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import io.awspring.cloud.messaging.config.QueueMessageHandlerFactory
import io.awspring.cloud.messaging.config.SimpleMessageListenerContainerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver

@Configuration
class SqsConfig {
    @Value("\${sfn.aws.sqs.endpoint}")
    private val endpoint: String? = null

    @Value("\${sfn.aws.region}")
    private val region: String? = null

    @Value("\${sfn.aws.message.access-key}")
    private val accessKey: String? = null

    @Value("\${sfn.aws.message.secret-key}")
    private val accessSecret: String? = null

    @Bean
    @Primary
    fun amazonSQSAsync(): AmazonSQSAsync {
        val credentials = BasicAWSCredentials(accessKey, accessSecret)
        val endpointConfiguration = EndpointConfiguration(endpoint, region)

        return AmazonSQSAsyncClientBuilder
            .standard()
            .withEndpointConfiguration(endpointConfiguration)
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .build()
    }

    @Bean
    fun simpleMessageListenerContainerFactory(
        amazonSQSAsync: AmazonSQSAsync,
        @Qualifier("taskExecutor") taskExecutor: AsyncTaskExecutor
    ): SimpleMessageListenerContainerFactory {
        val factory = SimpleMessageListenerContainerFactory()
        factory.setAmazonSqs(amazonSQSAsync)
        factory.setAutoStartup(true)
        factory.setMaxNumberOfMessages(10)
        factory.setWaitTimeOut(10)
//        factory.setTaskExecutor(taskExecutor)
        return factory;
    }

    @Bean
    fun queueMessageHandlerFactory(): QueueMessageHandlerFactory {
        val factory = QueueMessageHandlerFactory()
        factory.setArgumentResolvers(
            listOf<HandlerMethodArgumentResolver>(
                PayloadMethodArgumentResolver(messageConverter())
            )
        )
        return factory
    }

    @Bean
    protected fun messageConverter(): MessageConverter {
        return MappingJackson2MessageConverter().apply {
            this.serializedPayloadClass = String::class.java
            this.isStrictContentTypeMatch = false
        }
    }
}

enum class MessageAttribute(val value: String, val type: Class<*>) {
    APPROXIMATE_RECEIVE_COUNT("ApproximateReceiveCount", Int::class.java),
    APPROXIMATE_FIRST_RECEIVE_TIMESTAMP("ApproximateFirstReceiveTimestamp", Long::class.java),
    MESSAGE_DEDUPLICATION_ID("MessageDeduplicationId", String::class.java),
    MESSAGE_GROUP_ID("MessageGroupId", String::class.java),
    SENDER_ID("SenderId", String::class.java),
    SENT_TIMESTAMP("SentTimestamp", Long::class.java),
    SEQUENCE_NUMBER("SequenceNumber", String::class.java);
}
