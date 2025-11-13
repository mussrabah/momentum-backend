package com.muss_coding.momentum_backend.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AuthRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
}