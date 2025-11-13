package com.muss_coding.momentum_backend

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestController {

    @GetMapping("/hello")
    fun getHello(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<String> {
        // userDetails.username IS the user's email (from our CustomUserDetailsService)
        val email = userDetails.username
        return ResponseEntity.ok("Hello, $email! You are authenticated!")
    }
}