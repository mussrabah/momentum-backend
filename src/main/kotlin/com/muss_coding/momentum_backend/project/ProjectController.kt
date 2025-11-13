package com.muss_coding.momentum_backend.project

import com.muss_coding.momentum_backend.project.dto.CreateProjectRequest
import com.muss_coding.momentum_backend.project.dto.ProjectResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val projectService: ProjectService
) {

    @PostMapping
    fun createProject(
        @RequestBody request: CreateProjectRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ProjectResponse> {
        val project = projectService.createProject(request, userDetails)
        return ResponseEntity.status(201).body(project) // 201 Created
    }

    @GetMapping
    fun getAllProjects(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<List<ProjectResponse>> {
        val projects = projectService.getAllProjectsForUser(userDetails)
        return ResponseEntity.ok(projects)
    }
}