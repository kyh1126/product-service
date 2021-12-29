package com.smartfoodnet.base

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.smartfoodnet.common.utils.Log
import io.awspring.cloud.messaging.config.QueueMessageHandlerFactory
import io.awspring.cloud.messaging.config.SimpleMessageListenerContainerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class LocalStackConfig {
    @Value("\${sfn.aws.region}")
    private val region: String? = null

    @Bean(initMethod = "start", destroyMethod = "stop")
    fun localStackContainer(): LocalStackContainer {
        log.info("Starting localstack...")

        // 이슈로 인한 image tag 고정(https://github.com/localstack/localstack/issues/4902)
        return LocalStackContainer(DockerImageName.parse("localstack/localstack:0.12.20"))
            .withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.SNS)
            .withEnv("DEFAULT_REGION", region)
    }

    @Bean
    @Primary
    fun amazonSQSAsync(): AmazonSQSAsync {
        val endpointConfiguration = AwsClientBuilder.EndpointConfiguration(
            localStackContainer().getEndpointOverride(LocalStackContainer.Service.SQS).toString(),
            region
        )

        return AmazonSQSAsyncClientBuilder
            .standard()
            .withCredentials(localStackContainer().defaultCredentialsProvider)
            .withEndpointConfiguration(endpointConfiguration)
            .build()
    }

    @Bean
    fun simpleMessageListenerContainerFactory(
        amazonSQSAsync: AmazonSQSAsync,
    ): SimpleMessageListenerContainerFactory {
        val factory = SimpleMessageListenerContainerFactory()
        factory.setAmazonSqs(amazonSQSAsync)
        factory.setWaitTimeOut(20) // queue 에 메세지가 없을 때, 메시지가 들어올 때까지 Long polling 하는 시간
        factory.setMaxNumberOfMessages(10)
        return factory
    }

    @Bean
    fun queueMessageHandlerFactory(messageConverter: MessageConverter): QueueMessageHandlerFactory {
        val factory = QueueMessageHandlerFactory()
        factory.setArgumentResolvers(
            listOf<HandlerMethodArgumentResolver>(PayloadMethodArgumentResolver(messageConverter))
        )
        return factory
    }

    @Bean
    protected fun messageConverter(objectMapper: ObjectMapper): MessageConverter {
        return MappingJackson2MessageConverter().apply {
            this.objectMapper = objectMapper
            this.isStrictContentTypeMatch = false
        }
    }

    companion object : Log
}
