package com.example.wellness.hateos.user;


import com.example.wellness.controllers.UserController;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.UserBody;
import com.example.wellness.dto.common.UserDto;
import com.example.wellness.enums.Role;
import com.example.wellness.hateos.CustomEntityModel;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.Map;
import java.util.Set;


@RequiredArgsConstructor
@Component
public class UserDtoAssembler implements ReactiveRepresentationModelAssembler<UserDto> {


//    @Override
//    public Mono<EntityModel<UserDto>> toModel(@NonNull UserDto userDto) {
//        return Mono.just(EntityModel.of(userDto))
//                .flatMap(model ->
//                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(UserController.class).getUser(userDto.getId()))
//                                .withSelfRel()
//                                .toMono()
//                                .doOnNext(model::add).then(Mono.just(model)))
//                .flatMap(model ->
//                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(UserController.class).updateUser(userDto.getId(), UserBody.builder()
//                                        .firstName("New first name").lastName("New last name")
//                                        .build()))
//                                .withRel("updateUser")
//                                .toMono()
//                                .doOnNext(model::add).then(Mono.just(model)))
//                .flatMap(model ->
//                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(UserController.class).getAllUsers(
//                                        PageableBody.builder()
//                                                .page(0)
//                                                .size(10)
//                                                .sortingCriteria(Map.of("email", "asc"))
//                                                .build(), Set.of(Role.ROLE_USER, Role.ROLE_TRAINER)
//                                ))
//                                .withRel(IanaLinkRelations.COLLECTION)
//                                .toMono()
//                                .doOnNext(model::add).then(Mono.just(model)))
//                .flatMap(model ->
//                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(UserController.class).makeTrainer(userDto.getId()))
//                                .withRel("makeTrainer")
//                                .toMono()
//                                .doOnNext(model::add).then(Mono.just(model)));
//
//    }

    @Override
    public Mono<CustomEntityModel<UserDto>> toModel(UserDto entity) {
        return Mono.just(CustomEntityModel.<UserDto>builder()
                        .content(entity)
                        .build())
                .flatMap(model ->
                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(UserController.class).getUser(entity.getId()))
                                .withSelfRel()
                                .toMono()
                                .doOnNext(model::add).then(Mono.just(model)))
                .flatMap(model ->
                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(UserController.class).updateUser(entity.getId(), UserBody.builder()
                                        .firstName("New first name").lastName("New last name")
                                        .build()))
                                .withRel("updateUser")
                                .toMono()
                                .doOnNext(model::add).then(Mono.just(model)))
                .flatMap(model ->
                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(UserController.class).getAllUsers(
                                        PageableBody.builder()
                                                .page(0)
                                                .size(10)
                                                .sortingCriteria(Map.of("email", "asc"))
                                                .build(), "raz", Set.of(Role.ROLE_USER, Role.ROLE_TRAINER)
                                ))
                                .withRel(IanaLinkRelations.COLLECTION)
                                .toMono()
                                .doOnNext(model::add).then(Mono.just(model)))
                .flatMap(model ->
                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(UserController.class).makeTrainer(entity.getId()))
                                .withRel("makeTrainer")
                                .toMono()
                                .doOnNext(model::add).then(Mono.just(model)));
    }


}
