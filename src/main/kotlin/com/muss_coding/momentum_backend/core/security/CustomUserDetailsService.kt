package com.muss_coding.momentum_backend.core.security

import com.muss_coding.momentum_backend.auth.AuthRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val authRepository: AuthRepository
) : UserDetailsService {

    /**
     * Loads a user by their email address (which we use as the "username" in Spring Security)
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val user = authRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found with email: $username")

        // Spring Security's User object. We give it the email, the HASHED password,
        // and an empty list of authorities (roles), which we'll add later.
        return User.builder()
            .username(user.email)
            .password(user.passwordHash)
            .authorities(emptyList())
            .build()
    }
}