package com.muss_coding.momentum_backend.pomodoro

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface PomodoroRepository : JpaRepository<PomodoroSession, UUID> {
    // Count all "FOCUS" sessions for a specific user
    fun countAllByOwnerIdAndType(ownerId: UUID, type: String = "FOCUS"): Int

    // This is a native PostgreSQL query to get daily session counts for the last 7 days
    // It groups sessions by their date and counts them.
    @Query(
        value = """
            SELECT
                date_trunc('day', ended_at) as "date",
                COUNT(*) as "sessionCount"
            FROM
                pomodoro_sessions
            WHERE
                owner_id = :ownerId
                AND type = 'FOCUS'
                AND ended_at >= :startDate
            GROUP BY
                date_trunc('day', ended_at)
            ORDER BY
                "date" DESC
        """,
        nativeQuery = true
    )
    fun getDailyFocusStats(ownerId: UUID, startDate: Instant): List<Map<String, Any>>
}