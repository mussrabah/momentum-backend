package com.muss_coding.momentum_backend.task.dto

import com.muss_coding.momentum_backend.task.Task
import com.muss_coding.momentum_backend.task.TaskStatus
import java.time.Instant
import java.util.UUID

// DTO for creating a new task
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    val pomodoroEstimate: Int = 1,
    val deadline: Instant? = null
)

// DTO for returning a task
data class TaskResponse(
    val id: UUID,
    val title: String,
    val description: String?,
    val pomodoroEstimate: Int,
    val status: TaskStatus,
    val deadline: Instant?,
    val projectId: UUID, // Include the parent project ID
    val createdAt: Instant,
    val updatedAt: Instant
)

// Mapper function
fun Task.toTaskResponse(): TaskResponse {
    return TaskResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        pomodoroEstimate = this.pomodoroEstimate,
        status = this.status,
        deadline = this.deadline,
        projectId = this.project.id,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}