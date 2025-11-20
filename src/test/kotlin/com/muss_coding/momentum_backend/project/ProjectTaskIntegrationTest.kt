package com.muss_coding.momentum_backend.project

import com.muss_coding.momentum_backend.BaseIntegrationTest
import com.muss_coding.momentum_backend.auth.dto.RegisterRequest
import com.muss_coding.momentum_backend.project.dto.CreateProjectRequest
import com.muss_coding.momentum_backend.task.dto.CreateTaskRequest
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ProjectTaskIntegrationTest : BaseIntegrationTest() {

    // Helper to register and get token
    private fun getTokenFor(email: String): String {
        val request = RegisterRequest("User $email", email, "password")
        val result = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andReturn()

        return JsonPath.read(result.response.contentAsString, "$.accessToken")
    }

    @Test
    fun `should create project and add task`() {
        val token = getTokenFor("builder@example.com")

        // 1. Create Project
        val projectRequest = CreateProjectRequest("New Project", "Desc")
        val projectResult = mockMvc.perform(
            post("/api/projects")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("New Project"))
            .andReturn()

        val projectId = JsonPath.read<String>(projectResult.response.contentAsString, "$.id")

        // 2. Create Task in Project
        val taskRequest = CreateTaskRequest("New Task", pomodoroEstimate = 3)
        mockMvc.perform(
            post("/api/projects/$projectId/tasks")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("New Task"))
            .andExpect(jsonPath("$.pomodoroEstimate").value(3))
    }

    @Test
    fun `should NOT allow other user to access project tasks`() {
        val tokenA = getTokenFor("userA@example.com")
        val tokenB = getTokenFor("userB@example.com")

        // 1. User A creates project
        val projectRequest = CreateProjectRequest("User A Project")
        val result = mockMvc.perform(
            post("/api/projects")
                .header("Authorization", "Bearer $tokenA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequest))
        ).andReturn()

        val projectId = JsonPath.read<String>(result.response.contentAsString, "$.id")

        // 2. User B tries to add task to User A's project
        val taskRequest = CreateTaskRequest("Hacker Task")
        mockMvc.perform(
            post("/api/projects/$projectId/tasks")
                .header("Authorization", "Bearer $tokenB") // Using Token B
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest))
        )
            .andExpect(status().isInternalServerError) // Or 403/400 depending on implementation
    }
}