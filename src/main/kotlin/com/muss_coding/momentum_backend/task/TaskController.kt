package com.muss_coding.momentum_backend.task

import com.muss_coding.momentum_backend.task.dto.CreateTaskRequest
import com.muss_coding.momentum_backend.task.dto.TaskResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
// Notice the path: all task routes are "under" /api/projects
@RequestMapping("/api/projects/{projectId}/tasks")
class TaskController(
    private val taskService: TaskService
) {

    @PostMapping
    fun createTask(
        @PathVariable projectId: UUID, // Get the projectId from the URL
        @RequestBody request: CreateTaskRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<TaskResponse> {
        val task = taskService.createTask(projectId, request, userDetails)
        return ResponseEntity.status(201).body(task)
    }

    @GetMapping
    fun getAllTasksForProject(
        @PathVariable projectId: UUID, // Get the projectId from the URL
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.getAllTasksForProject(projectId, userDetails)
        return ResponseEntity.ok(tasks)
    }
}