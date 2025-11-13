package com.muss_coding.momentum_backend.project.dto

import com.muss_coding.momentum_backend.project.Project
import java.time.Instant
import java.util.UUID

// DTO for creating a new project
data class CreateProjectRequest(
    val title: String,
    val description: String? = null,
    val color: String? = null,
    val deadline: Instant? = null
)

// DTO for returning a project to the client
data class ProjectResponse(
    val id: UUID,
    val title: String,
    val description: String?,
    val color: String?,
    val deadline: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant
)

// Mapper function
fun Project.toProjectResponse(): ProjectResponse {
    return ProjectResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        color = this.color,
        deadline = this.deadline,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}