package com.smartfoodnet.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import javax.validation.Validator

@Configuration
class ValidationConfig {
    @Bean
    fun validator(): Validator {
        return LocalValidatorFactoryBean()
    }

    @Bean
    fun methodValidationPostProcessor(): MethodValidationPostProcessor {
        val methodValidationPostProcessor = MethodValidationPostProcessor()
        methodValidationPostProcessor.setValidator(validator())
        return methodValidationPostProcessor
    }
}
