package com.example.wellness.jwt;

import com.example.wellness.exceptions.notFound.TokenNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthManager implements ReactiveAuthenticationManager {

    private final JwtUtils jwtUtils;


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .map(auth -> jwtUtils.extractAllClaims(auth.getCredentials().toString()))
                .log()
                .onErrorResume(e -> Mono.error(new TokenNotFound()))
                .map(claims -> new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        null,
                        Stream.of(claims.get("roles"))
                                .map(roles -> (List<Map<String, String>>) roles) // List<Map<authority:ROLE_USER>>
                                .flatMap(role -> role.stream()
                                        .map(r -> r.get("authority"))
                                        .map(SimpleGrantedAuthority::new))
                                .toList()
                ));


    }
}
// roles:[{
// authority:ROLE_USER}]
