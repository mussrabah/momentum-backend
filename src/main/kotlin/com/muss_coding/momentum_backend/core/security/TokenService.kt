package com.muss_coding.momentum_backend.core.security

import com.muss_coding.momentum_backend.auth.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

interface TokenService {
    fun generateAccessToken(user: User): String
    fun generateRefreshToken(user: User): String
    // We'll need these functions soon
    fun getClaims(token: String): Claims
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean
    fun getSubject(token: String): String
}

@Service
class JwtTokenService(
    private val jwtProperties: JwtProperties
) : TokenService {

    // 1. Create a secure, thread-safe signing key
    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    override fun generateAccessToken(user: User): String {
        return generateToken(
            user = user,
            expiration = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpirationMs)
        )
    }

    override fun generateRefreshToken(user: User): String {
        return generateToken(
            user = user,
            expiration = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpirationMs)
        )
    }

    override fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    override fun getSubject(token: String): String {
        return getClaims(token).subject
    }

    override fun isTokenValid(token: String, userDetails: UserDetails): Boolean { // Changed 'user: User'
        return try {
            val claims = getClaims(token)
            val subject = claims.subject
            val isExpired = claims.expiration.before(Date())

            // Use userDetails.username, which we know is the email
            subject == userDetails.username && !isExpired
        } catch (e: Exception) {
            false
        }
    }

    private fun generateToken(user: User, expiration: Date): String {
        return Jwts.builder()
            .subject(user.email) // The "subject" of the token is the user's unique email
            .claim("name", user.name ?: "") // Add custom claims
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }
}