package com.muss_coding.momentum_backend.project

import com.muss_coding.momentum_backend.auth.AuthRepository
import com.muss_coding.momentum_backend.project.dto.CreateProjectRequest
import com.muss_coding.momentum_backend.project.dto.ProjectResponse
import com.muss_coding.momentum_backend.project.dto.toProjectResponse
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.Instant

interface ProjectService {
    fun createProject(request: CreateProjectRequest, userDetails: UserDetails): ProjectResponse
    fun getAllProjectsForUser(userDetails: UserDetails): List<ProjectResponse>
}

@Service
class ProjectServiceImpl(
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthRepository // We need this to find the User entity
) : ProjectService {

    override fun createProject(request: CreateProjectRequest, userDetails: UserDetails): ProjectResponse {
        // 1. Find the User entity for the currently logged-in user
        val user = authRepository.findByEmail(userDetails.username)
            ?: throw IllegalStateException("User not found, authentication is broken.")

        // 2. Create the new Project
        val project = Project(
            title = request.title,
            description = request.description,
            color = request.color,
            deadline = request.deadline,
            owner = user, // Assign the owner
            updatedAt = Instant.now()
        )

        // 3. Save and return it as a DTO
        val savedProject = projectRepository.save(project)
        return savedProject.toProjectResponse()
    }

    override fun getAllProjectsForUser(userDetails: UserDetails): List<ProjectResponse> {
        // 1. Find all projects by the owner's email
        return projectRepository.findAllByOwnerEmail(userDetails.username)
            .map { it.toProjectResponse() } // 2. Convert each one to a DTO
    }
}