package com.example.wellness.repositories;

import com.example.wellness.models.user.JwtToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface JwtTokenRepository extends R2dbcRepository<JwtToken, Long> {

    Mono<JwtToken> findByToken(String token);

    Flux<JwtToken> findAllByUserId(Long userId);

}
