package com.muss_coding.momentum_backend.auth

import com.muss_coding.momentum_backend.BaseIntegrationTest
import com.muss_coding.momentum_backend.auth.dto.LoginRequest
import com.muss_coding.momentum_backend.auth.dto.RegisterRequest
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `should register a new user`() {
        val request = RegisterRequest("Test User", "test@example.com", "password123")

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.user.email").value("test@example.com"))
    }

    @Test
    fun `should login successfully`() {
        // 1. Register first
        val registerRequest = RegisterRequest("Test User", "login@example.com", "password123")
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )

        // 2. Login
        val loginRequest = LoginRequest("login@example.com", "password123")
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").exists())
    }

    @Test
    fun `should fail login with wrong password`() {
        // 1. Register
        val registerRequest = RegisterRequest("Test User", "wrong@example.com", "password123")
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )

        // 2. Try login with wrong password
        val loginRequest = LoginRequest("wrong@example.com", "wrongpass")
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isForbidden)
            //.andExpect(status().isInternalServerError) // Or 401/403 depending on how we handled exceptions
    }
}