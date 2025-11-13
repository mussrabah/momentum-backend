package com.muss_coding.momentum_backend.task

import com.muss_coding.momentum_backend.project.ProjectRepository
import com.muss_coding.momentum_backend.task.dto.CreateTaskRequest
import com.muss_coding.momentum_backend.task.dto.TaskResponse
import com.muss_coding.momentum_backend.task.dto.toTaskResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

interface TaskService {
    fun createTask(projectId: UUID, request: CreateTaskRequest, userDetails: UserDetails): TaskResponse
    fun getAllTasksForProject(projectId: UUID, userDetails: UserDetails): List<TaskResponse>
}

@Service
class TaskServiceImpl(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository
) : TaskService {

    override fun createTask(projectId: UUID, request: CreateTaskRequest, userDetails: UserDetails): TaskResponse {
        // 1. Find the project and verify the user owns it
        val project = getProjectIfOwnedByUser(projectId, userDetails)

        // 2. Create the task
        val task = Task(
            title = request.title,
            description = request.description,
            pomodoroEstimate = request.pomodoroEstimate,
            deadline = request.deadline,
            project = project, // Assign the project
            updatedAt = Instant.now()
        )

        // 3. Save and return DTO
        val savedTask = taskRepository.save(task)
        return savedTask.toTaskResponse()
    }

    override fun getAllTasksForProject(projectId: UUID, userDetails: UserDetails): List<TaskResponse> {
        // 1. Verify the user owns the project before showing its tasks
        getProjectIfOwnedByUser(projectId, userDetails)

        // 2. If they do, find all tasks for that project
        return taskRepository.findAllByProjectId(projectId)
            .map { it.toTaskResponse() }
    }

    // A private helper function for our security check
    private fun getProjectIfOwnedByUser(projectId: UUID, userDetails: UserDetails): com.muss_coding.momentum_backend.project.Project {
        val project = projectRepository.findByIdOrNull(projectId)
        // TODO: Replace with custom exception
            ?: throw IllegalStateException("Project not found")

        if (project.owner.email != userDetails.username) {
            // TODO: Replace with AccessDeniedException
            throw IllegalStateException("You do not have permission to access this project")
        }
        return project
    }
}