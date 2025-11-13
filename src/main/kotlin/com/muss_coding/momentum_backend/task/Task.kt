package com.muss_coding.momentum_backend.task

import com.muss_coding.momentum_backend.project.Project
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

// We can define the task status as an enum
enum class TaskStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}

@Entity
@Table(name = "tasks")
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var title: String,

    @Column(nullable = true)
    var description: String? = null,

    // --- Fields from your "Momentum" spec ---
    @Column(name = "pomodoro_estimate", nullable = false)
    var pomodoroEstimate: Int = 1, // Default to 1 pomodoro

    @Enumerated(EnumType.STRING) // Store the status as a string (e.g., "IN_PROGRESS")
    @Column(nullable = false)
    var status: TaskStatus = TaskStatus.NOT_STARTED,

    @Column(nullable = true)
    var deadline: Instant? = null,
    // ------------------------------------------

    // This is the critical link
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    val project: Project,

    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
)