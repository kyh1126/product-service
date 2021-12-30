package com.smartfoodnet.config.aws

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import io.awspring.cloud.messaging.config.SimpleMessageListenerContainerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@Profile("!test")
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
        @Qualifier("taskExecutor") taskExecutor: ThreadPoolTaskExecutor
    ): SimpleMessageListenerContainerFactory {
        val factory = SimpleMessageListenerContainerFactory()
        factory.setAmazonSqs(amazonSQSAsync)
        factory.setWaitTimeOut(20) // queue 에 메세지가 없을 때, 메시지가 들어올 때까지 Long polling 하는 시간
        factory.setMaxNumberOfMessages(10)
        factory.setTaskExecutor(taskExecutor)
        return factory
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
