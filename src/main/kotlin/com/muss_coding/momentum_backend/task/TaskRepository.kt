package com.muss_coding.momentum_backend.task

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface TaskRepository : JpaRepository<Task, UUID> {
    // Find all tasks for a specific project
    fun findAllByProjectId(projectId: UUID): List<Task>

    // Count all completed tasks for a user
    // We join through the project to get to the owner
    fun countByProjectOwnerIdAndStatus(ownerId: UUID, status: TaskStatus): Int

    // Find the top 5 upcoming, incomplete tasks for a user, ordered by the deadline
    @Query(
        value = """
            SELECT t FROM Task t
            WHERE t.project.owner.id = :ownerId
            AND t.status != 'COMPLETED'
            AND t.deadline IS NOT NULL
            AND t.deadline > :now
            ORDER BY t.deadline ASC
            LIMIT 5
        """
    )
    fun findUpcomingDeadlines(ownerId: UUID, now: Instant): List<Task>
}