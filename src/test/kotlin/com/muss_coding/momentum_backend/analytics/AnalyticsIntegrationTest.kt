package com.muss_coding.momentum_backend.analytics

import com.muss_coding.momentum_backend.BaseIntegrationTest
import com.muss_coding.momentum_backend.auth.dto.RegisterRequest
import com.muss_coding.momentum_backend.pomodoro.dto.LogPomodoroRequest
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.time.temporal.ChronoUnit

class AnalyticsIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `should calculate analytics summary correctly`() {
        // 1. Register
        val regRequest = RegisterRequest("Analytics User", "stats@example.com", "password")
        val result = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest))
        ).andReturn()
        val token = JsonPath.read<String>(result.response.contentAsString, "$.accessToken")

        // 2. Log some Pomodoro sessions
        // Session 1: Started 1 hour ago
        val session1 = LogPomodoroRequest(taskId = null, startedAt = Instant.now().minus(1, ChronoUnit.HOURS))
        mockMvc.perform(
            post("/api/pomodoro/sessions")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(session1))
        ).andExpect(status().isCreated)

        // Session 2: Started 2 hours ago
        val session2 = LogPomodoroRequest(taskId = null, startedAt = Instant.now().minus(2, ChronoUnit.HOURS))
        mockMvc.perform(
            post("/api/pomodoro/sessions")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(session2))
        ).andExpect(status().isCreated)

        // 3. Get Analytics
        mockMvc.perform(
            get("/api/analytics/summary")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalPomodoros").value(2))
            // 2 sessions * 25 mins = 50 mins = 0.83 hours
            .andExpect(jsonPath("$.totalHoursFocused").value(0.83))
    }
}