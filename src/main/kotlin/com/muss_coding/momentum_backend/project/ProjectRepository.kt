package com.muss_coding.momentum_backend.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProjectRepository : JpaRepository<Project, UUID> {
    // We need this to find all projects for a specific user
    fun findAllByOwnerEmail(email: String): List<Project>
}