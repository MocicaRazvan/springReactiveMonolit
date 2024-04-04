package com.example.wellness.filters;

import com.example.wellness.exceptions.notFound.TokenNotFound;
import com.example.wellness.jwt.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter implements WebFilter {

    private final JwtUtils jwtUtils;
    private final ReactiveUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    private final String[] AUTH_WHITELIST = {
            "/auth/login", "/auth/register",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/**",
            "/webjars/**",
            "/favicon.ico",
//            "/demo/**"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            log.error("Request: {}", exchange.getRequest().getMethod());
            return chain.filter(exchange);

        }

        try {

            AntPathMatcher pathMatcher = new AntPathMatcher();
            String path = exchange.getRequest().getPath().value();


            if (Arrays.stream(AUTH_WHITELIST).anyMatch(route -> pathMatcher.match(route, path))) {
                return chain.filter(exchange);
            }

            final String authCookie = exchange.getRequest().getCookies().getFirst("authToken") != null ?
                    Objects.requireNonNull(exchange.getRequest().getCookies().getFirst("authToken")).getValue() : null;

            log.info("AuthCookie: {}", authCookie);

            final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            log.error(exchange.getRequest().getHeaders().toString());

            log.info("AuthHeader: {}", authHeader);

            if ((authHeader == null || !authHeader.startsWith("Bearer ")) && (authCookie == null || authCookie.isEmpty())) {
                return handleError("Token not found", exchange);
            }

            final String token = authHeader != null ? authHeader.substring(7) : authCookie;

            final String email = jwtUtils.extractUsername(token);

            if (email == null) {
                return handleError("Invalid token", exchange);
            }

            return userDetailsService.findByUsername(email)
                    .switchIfEmpty(Mono.error(new TokenNotFound()))
                    .flatMap(ud -> jwtUtils.isTokenValid(token, ud.getUsername()))
                    .filter(Boolean::booleanValue)
                    .switchIfEmpty(Mono.error(new TokenNotFound()))
                    .then(Mono.defer(() -> {
                        exchange.getAttributes().put("token", token);
                        return chain.filter(exchange);
                    }));

        } catch (UsernameNotFoundException e) {
            return handleError("User not found", exchange);
        } catch (TokenNotFound e) {
            return handleError("Token not found", exchange);
        } catch (Exception e) {
            return handleError("Internal server error", exchange);
        }
    }

    private Mono<Void> handleError(String message, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", message);
        resp.put("timestamp", Instant.now().toString());
        resp.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        resp.put("status", HttpStatus.UNAUTHORIZED.value());
        resp.put("path", exchange.getRequest().getPath().value());
        try {
            return response.writeWith(Mono.just(response.bufferFactory().wrap(objectMapper.writeValueAsBytes(resp))));
        } catch (Exception e) {
            log.error("Error while writing response", e);
            return Mono.error(e);
        }
    }
}
