package com.example.wellness.services;

import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.UserBody;
import com.example.wellness.dto.common.UserDto;
import com.example.wellness.enums.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface UserService {
    Mono<UserDto> getUser(Long id);

    Flux<PageableResponse<UserDto>> getAllUsers(PageableBody pageableBody, Set<Role> roles, String email);

    Mono<UserDto> makeTrainer(Long id);

    Mono<UserDto> updateUser(Long id, UserBody userBody);
}
