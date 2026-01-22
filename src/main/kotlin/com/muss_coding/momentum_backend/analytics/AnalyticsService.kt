package com.muss_coding.momentum_backend.analytics

import com.muss_coding.momentum_backend.analytics.dto.AnalyticsResponse
import com.muss_coding.momentum_backend.analytics.dto.DailyFocusStat
import com.muss_coding.momentum_backend.analytics.dto.TaskDeadlineStat
import com.muss_coding.momentum_backend.auth.AuthRepository
import com.muss_coding.momentum_backend.pomodoro.PomodoroRepository
import com.muss_coding.momentum_backend.task.TaskRepository
import com.muss_coding.momentum_backend.task.TaskStatus
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

interface AnalyticsService {
    fun getAnalyticsSummary(userDetails: UserDetails): AnalyticsResponse
}

@Service
class AnalyticsServiceImpl(
    private val authRepository: AuthRepository,
    private val pomodoroRepository: PomodoroRepository,
    private val taskRepository: TaskRepository
) : AnalyticsService {

    private val POMODORO_DURATION_MINUTES = 25.0

    override fun getAnalyticsSummary(userDetails: UserDetails): AnalyticsResponse {
        val user = authRepository.findByEmail(userDetails.username)
            ?: throw IllegalStateException("User not found")

        val now = Instant.now()
        val sevenDaysAgo = now.minus(7, ChronoUnit.DAYS)

        // 1. Get total Pomodoros and calculate focused hours
        val totalPomodoros = pomodoroRepository.countAllByOwnerIdAndType(user.id!!, "FOCUS")
        val totalHoursFocused = (totalPomodoros * POMODORO_DURATION_MINUTES) / 60.0

        // 2. Get total completed tasks
        val totalTasksCompleted = taskRepository.countByProjectOwnerIdAndStatus(user.id, TaskStatus.COMPLETED)

        // 3. Get daily focus stats
        // REPLACE WITH THIS:
        val dailyFocusStats = pomodoroRepository.getDailyFocusStats(user.id, sevenDaysAgo)
            .map {
                DailyFocusStat(
                    date = (it["date"] as Instant), // This is the fixed line
                    sessionCount = (it["sessionCount"] as Long).toInt()
                )
            }

        // 4. Calculate average pomodoros per day (simple avg over 7 days)
        val avgPomodoros = if (dailyFocusStats.isEmpty()) 0.0 else dailyFocusStats.sumOf { it.sessionCount } / 7.0

        // 5. Get upcoming deadlines
        val upcomingDeadlines = taskRepository.findUpcomingDeadlines(user.id, now)
            .map {
                TaskDeadlineStat(
                    taskId = it.id,
                    title = it.title,
                    deadline = it.deadline!!,
                    projectId = it.project.id
                )
            }

        return AnalyticsResponse(
            totalPomodoros = totalPomodoros,
            totalHoursFocused = totalHoursFocused.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble(),
            totalTasksCompleted = totalTasksCompleted,
            averagePomodorosPerDay = avgPomodoros.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble(),
            dailyFocus = dailyFocusStats,
            upcomingDeadlines = upcomingDeadlines
        )
    }
}