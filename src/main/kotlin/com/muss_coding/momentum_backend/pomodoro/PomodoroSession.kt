package com.muss_coding.momentum_backend.pomodoro

import com.muss_coding.momentum_backend.auth.User
import com.muss_coding.momentum_backend.task.Task
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "pomodoro_sessions")
data class PomodoroSession(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    // The user who completed this session
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    val owner: User,

    // The task this session was for (can be null if it was just a general session)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = true)
    val task: Task?,

    @Column(name = "started_at", nullable = false)
    val startedAt: Instant,

    @Column(name = "ended_at", nullable = false)
    val endedAt: Instant,

    // e.g., "FOCUS", "SHORT_BREAK", "LONG_BREAK"
    @Column(nullable = false)
    val type: String = "FOCUS"
)