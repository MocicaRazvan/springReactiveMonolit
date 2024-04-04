package com.example.wellness.controllers;


import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.UserBody;
import com.example.wellness.dto.common.UserDto;
import com.example.wellness.enums.Role;
import com.example.wellness.hateos.CustomEntityModel;
import com.example.wellness.hateos.user.PageableUserAssembler;
import com.example.wellness.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users Controller")
@Slf4j
public class UserController {

    private final UserService userService;
    private final PageableUserAssembler pageableUserAssembler;


    @GetMapping(value = "/roles", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE,
            MediaType.APPLICATION_NDJSON_VALUE})
    public Mono<ResponseEntity<List<Role>>> getRoles() {
        return Mono.just(ResponseEntity.ok(List.of(Role.values())));
    }


    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<UserDto>>> getUser(@PathVariable Long id) {
        return userService.getUser(id)
                .flatMap(u -> pageableUserAssembler.getItemAssembler().toModel(u))
                .map(ResponseEntity::ok);
    }


    @PatchMapping(produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<PageableResponse<CustomEntityModel<UserDto>>> getAllUsers(
            @Valid @RequestBody PageableBody pageableBody, @RequestParam(required = false) String email,
            @RequestParam(required = false) Set<Role> roles
    ) {
        log.error(roles.toString());
        return userService.getAllUsers(pageableBody, roles, email)
                .flatMap(pageableUserAssembler::toModel);
    }

    @PatchMapping(value = "/admin/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<UserDto>>> makeTrainer(@PathVariable Long id) {
        return userService.makeTrainer(id)
                .flatMap(u -> pageableUserAssembler.getItemAssembler().toModel(u))
                .map(ResponseEntity::ok);
    }

    @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<UserDto>>> updateUser(@PathVariable Long id, @Valid @RequestBody UserBody userBody) {
        return userService.updateUser(id, userBody)
                .flatMap(u -> pageableUserAssembler.getItemAssembler().toModel(u))
                .map(ResponseEntity::ok);
    }


}
