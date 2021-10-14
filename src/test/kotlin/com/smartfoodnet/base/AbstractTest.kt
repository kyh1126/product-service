package com.smartfoodnet.base

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractTest {

    companion object {
        /**
         * 테스트(PER_CLASS)와 도커 컨테이너 라이프사이클 따로가기 위해 @Container 설정 제거
         * <p>
         * @see <a href="https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/">@Container 제거한 이유</a>
         */
        private val mySqlContainer: MySQLContainer<*> = MySQLContainer<Nothing>("mysql:latest").apply {
            withCommand("mysqld", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_0900_ai_ci")
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { mySqlContainer.jdbcUrl }
            registry.add("spring.datasource.username") { mySqlContainer.username }
            registry.add("spring.datasource.password") { mySqlContainer.password }
            registry.add("spring.datasource.driver-class-name") { mySqlContainer.driverClassName }
        }
    }
}
