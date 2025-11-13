package com.muss_coding.momentum_backend.auth

import com.muss_coding.momentum_backend.auth.dto.AuthResponse
import com.muss_coding.momentum_backend.auth.dto.LoginRequest
import com.muss_coding.momentum_backend.auth.dto.RegisterRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val authResponse = authService.register(request)
        // Returns HTTP 201 Created
        return ResponseEntity.status(201).body(authResponse)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val authResponse = authService.login(request)
        return ResponseEntity.ok(authResponse)
    }
}