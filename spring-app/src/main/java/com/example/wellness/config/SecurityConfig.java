package com.example.wellness.config;

import com.example.wellness.enums.Role;
import com.example.wellness.filters.AuthFilter;
import com.example.wellness.repositories.JwtTokenRepository;
import com.example.wellness.repositories.SecurityContextRepository;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.WebFilter;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

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


    private final String[] TRAINER_LIST = {
            "/test/trainer",

            "/posts/create",
            "/posts/update/**",
            "/posts/trainer/**",
            "/posts/delete/**",

            "/exercises/create",
            "/exercises/update/**",
            "/exercises/trainer/**",
            "/exercises/delete/**",

            "/trainings/create",
            "/trainings/update/**",
            "/trainings/trainer/**",
            "/trainings/delete/**",

            "/orders/create",
            "/orders/trainer/**",

    };
    private final String[] ADMIN_LIST = {
            "/test/admin",
            "/posts/admin/**",
            "/exercises/admin/**",
            "/users/admin/**",
            "/trainings/admin/**",
            "/orders/admin/**",
    };


    private final SecurityContextRepository securityContextRepository;

    @Bean
    public ReactiveUserDetailsService userDetailsService(
            UserRepository userRepository
    ) {
        return username -> userRepository.findByEmail(username)
                .map(u -> (UserDetails) u)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with email " + username)));

    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // Adjust as necessary
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        );
        configuration.addAllowedHeader("*"); // Or specify explicitly
        // Specifically allow these headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Cache-Control", "Content-Type", "Accept", "X-Requested-With", "Origin"
        ));
        // Expose these headers in the response to the client
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin", "Authorization", "Content-Type"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setAllowPrivateNetwork(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServerLogoutHandler logoutHandler(
            JwtTokenRepository jwtTokenRepository
    ) {
        return (exchange, authentication) -> {
            ServerHttpRequest request = exchange.getExchange().getRequest();
            ServerHttpResponse response = exchange.getExchange().getResponse();

            final String authHeader = request.getHeaders().getFirst("Authorization");
            final String authCookie = request.getCookies().getFirst("authToken") != null ?
                    Objects.requireNonNull(request.getCookies().getFirst("authToken")).getValue() : null;

            if ((authHeader == null || !authHeader.startsWith("Bearer ")) && (authCookie == null || authCookie.isEmpty())) {
                return Mono.empty();
            }

            final String jwt = authHeader != null ? authHeader.substring(7) : authCookie;

            return jwtTokenRepository.findByToken(jwt)
                    .flatMap(savedToken -> {
                                savedToken.setRevoked(true);
                                return jwtTokenRepository.save(savedToken);
                            }
                    ).then(Mono.defer(
                            () -> {
                                response.setStatusCode(HttpStatus.OK);
                                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                                response.getCookies().set("authToken", ResponseCookie.from("authToken", "")
                                        .maxAge(0)
                                        .path("/")
                                        .httpOnly(true)
                                        .sameSite("Strict")
                                        .build());
                                return response.writeWith(
                                        Mono.just(
                                                response.bufferFactory().wrap("{\"message\": \"Logout successful\"}"
                                                        .getBytes()))
                                );
                            }
                    ));

        };
    }

    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler(
            JwtUtils jwtUtils
    ) {
        return (exchange, denied) ->
                jwtUtils.createResponse(
                        exchange,
                        HttpStatus.FORBIDDEN,
                        denied
                );

    }


    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity,
                                                         AuthFilter authFilter, JwtUtils jwtUtils, JwtTokenRepository jwtTokenRepository) {
        return httpSecurity
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                .authorizeExchange(authorizeExchangeSpec -> {
                    authorizeExchangeSpec.pathMatchers(AUTH_WHITELIST).permitAll()
                            .pathMatchers(HttpMethod.OPTIONS).permitAll()
                            .pathMatchers(TRAINER_LIST).hasAnyAuthority(Role.ROLE_TRAINER.name(), Role.ROLE_ADMIN.name())
                            .pathMatchers(ADMIN_LIST).hasAuthority(Role.ROLE_ADMIN.name())
                            .anyExchange().authenticated();
                })
                .addFilterAfter(authFilter, SecurityWebFiltersOrder.FIRST)
                .securityContextRepository(securityContextRepository)
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.accessDeniedHandler(accessDeniedHandler(jwtUtils))
                )
                .logout(
                        logoutSpec -> logoutSpec.logoutUrl("/auth/logout")
                                .logoutHandler(logoutHandler(jwtTokenRepository))
                                .requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/auth/logout"))
                                .logoutSuccessHandler((exchange, authentication) -> {
                                    ServerHttpResponse response = exchange.getExchange().getResponse();
                                    response.setStatusCode(HttpStatus.OK);
                                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                                    return response.writeWith(
                                            Mono.just(
                                                    response.bufferFactory().wrap("{\"message\": \"Logout successful\"}"
                                                            .getBytes()))
                                    );

                                })
                )
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
