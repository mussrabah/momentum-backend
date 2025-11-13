package com.muss_coding.momentum_backend.pomodoro

import com.muss_coding.momentum_backend.auth.AuthRepository
import com.muss_coding.momentum_backend.pomodoro.dto.LogPomodoroRequest
import com.muss_coding.momentum_backend.pomodoro.dto.PomodoroSessionResponse
import com.muss_coding.momentum_backend.pomodoro.dto.toPomodoroSessionResponse
import com.muss_coding.momentum_backend.task.Task
import com.muss_coding.momentum_backend.task.TaskRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.Instant

interface PomodoroService {
    fun logSession(request: LogPomodoroRequest, userDetails: UserDetails): PomodoroSessionResponse
}

@Service
class PomodoroServiceImpl(
    private val pomodoroRepository: PomodoroRepository,
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository
) : PomodoroService {

    override fun logSession(request: LogPomodoroRequest, userDetails: UserDetails): PomodoroSessionResponse {
        val user = authRepository.findByEmail(userDetails.username)
            ?: throw IllegalStateException("User not found")

        var task: Task? = null
        if (request.taskId != null) {
            // 1. If a taskId is provided, find the task
            task = taskRepository.findByIdOrNull(request.taskId)
                ?: throw IllegalStateException("Task not found")

            // 2. Security Check: Verify the user owns the project this task belongs to
            if (task.project.owner.id != user.id) {
                // TODO: Replace with AccessDeniedException
                throw IllegalStateException("You do not have permission to log sessions for this task")
            }
        }

        // 3. Create the session
        val session = PomodoroSession(
            owner = user,
            task = task,
            startedAt = request.startedAt,
            endedAt = Instant.now(), // The session ends "now" (when it's logged)
            type = request.type
        )

        // 4. Save and return DTO
        val savedSession = pomodoroRepository.save(session)
        return savedSession.toPomodoroSessionResponse()
    }
}