package com.example.wellness.services.impl;

import com.example.wellness.dto.auth.AuthResponse;
import com.example.wellness.dto.auth.LoginRequest;
import com.example.wellness.dto.auth.RegisterRequest;
import com.example.wellness.exceptions.UserWithEmailExists;
import com.example.wellness.jwt.JwtUtils;
import com.example.wellness.mappers.UserMapper;
import com.example.wellness.models.user.JwtToken;
import com.example.wellness.models.user.UserCustom;
import com.example.wellness.repositories.JwtTokenRepository;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final JwtTokenRepository jwtTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtil;

    @Override
    public Mono<AuthResponse> register(RegisterRequest registerRequest) {
        log.error(userMapper.fromRegisterRequestToUserCustom(registerRequest).toString());
        return userRepository.existsByEmail(registerRequest.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new UserWithEmailExists(registerRequest.getEmail()));
                    }
                    return userRepository.save(userMapper.fromRegisterRequestToUserCustom(registerRequest))
                            .flatMap(this::generateResponse);
                });
    }

    @Override
    public Mono<AuthResponse> login(LoginRequest loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with email: " + loginRequest.getEmail() + " not found")))
                .flatMap(u ->
                        revokeOldTokens(u).then(Mono.defer(() -> {
                            if (!passwordEncoder.matches(loginRequest.getPassword(), u.getPassword())) {
                                log.error("Password not match");
                                return Mono.error(new UsernameNotFoundException("User with email: " + loginRequest.getEmail() + " not found"));
                            }
                            return generateResponse(u);
                        }))
                );
    }


    private Mono<AuthResponse> generateResponse(UserCustom user) {
        JwtToken jwtToken = JwtToken.builder()
                .userId(user.getId())
                .token(jwtUtil.generateToken(user))
                .revoked(false)
                .build();
        return jwtTokenRepository.save(jwtToken)
                .map(t -> userMapper.fromUserCustomToAuthResponse(user).map(
                        u -> {
                            u.setToken(jwtToken.getToken());
                            return u;
                        }
                ));

    }

    private Mono<Void> revokeOldTokens(UserCustom user) {
        return jwtTokenRepository.findAllByUserId(user.getId())
                .flatMap(t -> {
                    t.setRevoked(true);
                    return jwtTokenRepository.save(t);
                })
                .then();

    }


}
