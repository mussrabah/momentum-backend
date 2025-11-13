package com.muss_coding.momentum_backend.auth.dto

import java.util.UUID

// Used for POST /api/auth/register
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

// Used for POST /api/auth/login
data class LoginRequest(
    val email: String,
    val password: String
)

// Returned on successful login or registration
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

// A "safe" version of the User entity
data class UserDto(
    val id: UUID,
    val email: String,
    val name: String?
)