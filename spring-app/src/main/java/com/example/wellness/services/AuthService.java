package com.example.wellness.services;

import com.example.wellness.dto.auth.AuthResponse;
import com.example.wellness.dto.auth.LoginRequest;
import com.example.wellness.dto.auth.RegisterRequest;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface AuthService {
    Mono<AuthResponse> register(RegisterRequest registerRequest);

    Mono<AuthResponse> login(LoginRequest loginRequest);
}
