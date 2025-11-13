package com.muss_coding.momentum_backend.core.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    var secret: String = "",
    var accessTokenExpirationMs: Long = 0,
    var refreshTokenExpirationMs: Long = 0
)