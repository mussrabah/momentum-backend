package com.muss_coding.momentum_backend.auth

import com.muss_coding.momentum_backend.auth.dto.AuthResponse
import com.muss_coding.momentum_backend.auth.dto.LoginRequest
import com.muss_coding.momentum_backend.auth.dto.RegisterRequest
import com.muss_coding.momentum_backend.auth.dto.toUserDto
import com.muss_coding.momentum_backend.core.security.TokenService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

interface AuthService {
    fun register(request: RegisterRequest): AuthResponse
    fun login(request: LoginRequest): AuthResponse
}

@Service
class AuthServiceImpl(
    private val authRepository: AuthRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService
) : AuthService {

    override fun register(request: RegisterRequest): AuthResponse {
        // 1. Check if user already exists
        if (authRepository.findByEmail(request.email) != null) {
            // We'll replace this with a proper custom exception later
            throw IllegalStateException("User with email ${request.email} already exists")
        }

        // 2. Create new user
        val user = User(
            name = request.name,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password)
        )

        println("user: $user")
        val savedUser = authRepository.save(user)

        // 3. Generate tokens
        val accessToken = tokenService.generateAccessToken(savedUser)
        val refreshToken = tokenService.generateRefreshToken(savedUser)

        // 4. Return response
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = savedUser.toUserDto()
        )
    }

    override fun login(request: LoginRequest): AuthResponse {
        // 1. Find user by email
        val user = authRepository.findByEmail(request.email)
        // TODO: Replace with custom exception
            ?: throw IllegalStateException("Invalid email or password")

        // 2. Check if passwords match
        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            // TODO: Replace with custom exception
            throw IllegalStateException("Invalid email or password")
        }

        // 3. Generate tokens
        val accessToken = tokenService.generateAccessToken(user)
        val refreshToken = tokenService.generateRefreshToken(user)

        // 4. Return response
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = user.toUserDto()
        )
    }
}