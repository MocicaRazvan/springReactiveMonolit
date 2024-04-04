package com.example.wellness.controllers;

import com.example.wellness.advices.AuthAdvice;
import com.example.wellness.dto.auth.AuthResponse;
import com.example.wellness.dto.auth.LoginRequest;
import com.example.wellness.dto.auth.RegisterRequest;
import com.example.wellness.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        return authService.register(registerRequest)
                .map(resp -> ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, createCookie(resp.getToken()).toString()).body(resp));
    }

    private ResponseCookie createCookie(String token) {
        return ResponseCookie.from("authToken", token)
                .httpOnly(true)
                .maxAge(Duration.ofDays(1))
                .path("/")
                .build();
    }

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return authService.login(loginRequest)
                .map(resp -> ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, createCookie(resp.getToken()).toString()).body(resp));
    }
}
