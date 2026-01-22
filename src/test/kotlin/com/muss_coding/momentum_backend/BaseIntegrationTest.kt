package com.muss_coding.momentum_backend

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc // <--- 1. Enables MockMvc
@ActiveProfiles("test")
abstract class BaseIntegrationTest {

    // 2. Inject MockMvc so subclasses can use it
    @Autowired
    lateinit var mockMvc: MockMvc

    // 3. Inject ObjectMapper so subclasses can convert objects to JSON
    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setUp() {
        clearDatabase()
    }

    fun clearDatabase() {
        // Truncate tables to ensure a clean state for every test
        jdbcTemplate.execute("TRUNCATE TABLE users, projects, tasks, pomodoro_sessions CASCADE")
    }

    companion object {
        // 4. Static Container (Singleton Pattern) to prevent "Port Mismatch" errors
        private val postgres = PostgreSQLContainer("postgres:16-alpine").apply {
            withDatabaseName("momentum_test")
            withUsername("test")
            withPassword("test")
            start() // Start only once
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerDBProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}