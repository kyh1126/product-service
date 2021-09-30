package com.smartfoodnet.base

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class AbstractTest {

    companion object {
        @Container
        private val mySqlContainer: MySQLContainer<*> = MySQLContainer<Nothing>("mysql:latest").apply {
            withCommand("mysqld", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_0900_ai_ci")
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", mySqlContainer::getJdbcUrl)
            registry.add("spring.datasource.username") { mySqlContainer.username }
            registry.add("spring.datasource.password") { mySqlContainer.password }
            registry.add("spring.datasource.driver-class-name") { mySqlContainer.driverClassName }
        }
    }
}
