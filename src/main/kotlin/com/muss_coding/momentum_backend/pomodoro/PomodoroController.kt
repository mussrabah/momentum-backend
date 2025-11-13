package com.muss_coding.momentum_backend.pomodoro

import com.muss_coding.momentum_backend.pomodoro.dto.LogPomodoroRequest
import com.muss_coding.momentum_backend.pomodoro.dto.PomodoroSessionResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/pomodoro")
class PomodoroController(
    private val pomodoroService: PomodoroService
) {

    @PostMapping("/sessions")
    fun logSession(
        @RequestBody request: LogPomodoroRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<PomodoroSessionResponse> {
        val session = pomodoroService.logSession(request, userDetails)
        return ResponseEntity.status(201).body(session)
    }
}