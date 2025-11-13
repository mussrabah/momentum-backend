package com.muss_coding.momentum_backend.core.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val tokenService: TokenService,
    private val userDetailsService: UserDetailsService // This will inject our CustomUserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        // 1. If no token is present, let the request pass.
        // Spring Security will block it later if the endpoint is secured.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        // 2. Extract the token
        val token = authHeader.substring(7) // "Bearer ".length
        val userEmail = tokenService.getSubject(token)

        // 3. If we have a user and they aren't already authenticated
        if (userEmail != null && SecurityContextHolder.getContext().authentication == null) {

            // 4. Load the user from the database
            val userDetails: UserDetails = userDetailsService.loadUserByUsername(userEmail)

            // 5. Validate the token
            if (tokenService.isTokenValid(token, userDetails)) {

                // 6. If valid, create an auth token and set it in the SecurityContext
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, // We don't need credentials
                    userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                // This is the line that officially "logs in" the user for this request
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        // 7. Pass the request to the next filter in the chain
        filterChain.doFilter(request, response)
    }
}