package com.muss_coding.momentum_backend.analytics.dto

import java.time.Instant
import java.util.UUID

// This is the main response object for our endpoint
data class AnalyticsResponse(
    val totalPomodoros: Int,
    val totalHoursFocused: Double,
    val totalTasksCompleted: Int,
    val averagePomodorosPerDay: Double,
    val dailyFocus: List<DailyFocusStat>, // A list of focus stats for the last 7 days
    val upcomingDeadlines: List<TaskDeadlineStat> // Top 5 tasks with upcoming deadlines
)

// Represents a single day in the focus chart
data class DailyFocusStat(
    val date: Instant,
    val sessionCount: Int
)

// Represents a task that is due soon
data class TaskDeadlineStat(
    val taskId: UUID,
    val title: String,
    val deadline: Instant,
    val projectId: UUID
)