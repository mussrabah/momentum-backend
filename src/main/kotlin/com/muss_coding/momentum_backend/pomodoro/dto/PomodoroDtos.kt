package com.muss_coding.momentum_backend.pomodoro.dto

import com.muss_coding.momentum_backend.pomodoro.PomodoroSession
import java.time.Instant
import java.util.UUID

// DTO for logging a new session
data class LogPomodoroRequest(
    val taskId: UUID?, // The task this session was for
    val startedAt: Instant,
    val type: String = "FOCUS"
)

// DTO for returning a session
data class PomodoroSessionResponse(
    val id: UUID,
    val ownerId: UUID,
    val taskId: UUID?,
    val startedAt: Instant,
    val endedAt: Instant,
    val type: String
)

// Mapper function
fun PomodoroSession.toPomodoroSessionResponse(): PomodoroSessionResponse {
    return PomodoroSessionResponse(
        id = this.id,
        ownerId = this.owner.id,
        taskId = this.task?.id,
        startedAt = this.startedAt,
        endedAt = this.endedAt,
        type = this.type
    )
}