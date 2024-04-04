package com.example.wellness.hateos.user;


import com.example.wellness.controllers.UserController;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.enums.Role;
import com.example.wellness.hateos.CustomEntityModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@Slf4j
public abstract class PageableResponseAssembler<T, D extends ReactiveRepresentationModelAssembler<T>> {

    private final D itemAssembler;


    public Mono<PageableResponse<CustomEntityModel<T>>> toModel(PageableResponse<T> pageableResponse, List<WebFluxLinkBuilder.WebFluxLink> additionalLinks) {
        return itemAssembler.toModel(pageableResponse.getContent())
                .map(c -> PageableResponse.<CustomEntityModel<T>>builder()
                        .content(c)
                        .pageInfo(pageableResponse.getPageInfo())
                        .links(additionalLinks)
                        .build());
    }


    public Mono<PageableResponse<CustomEntityModel<T>>> toModel(PageableResponse<T> pageableResponse) {
        List<WebFluxLinkBuilder.WebFluxLink> links = new ArrayList<>();
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(UserController.class).getAllUsers(
                PageableBody.builder()
                        .page(0)
                        .size(10)
                        .sortingCriteria(Map.of("email", "asc"))
                        .build(), "raz", Set.of(Role.ROLE_USER, Role.ROLE_TRAINER)
        )).withSelfRel());
        return toModel(pageableResponse, links);
    }

//    public Mono<PageableResponse<CustomEntityModel<T>>> toCustomModel(PageableResponse<T> pageableResponse, List<WebFluxLinkBuilder.WebFluxLink> additionalLinks) {
//        return itemAssembler.toCustomModel(pageableResponse.getContent())
//                .map(c -> PageableResponse.<CustomEntityModel<T>>builder()
//                        .content(c)
//                        .pageInfo(pageableResponse.getPageInfo())
//                        .links(additionalLinks)
//                        .build());
//    }


//    public Mono<PageableResponse<CustomEntityModel<T>>> toCustomModel(PageableResponse<T> pageableResponse) {
//        List<WebFluxLinkBuilder.WebFluxLink> links = new ArrayList<>();
//        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(UserController.class).getAllUsers(
//                PageableBody.builder()
//                        .page(0)
//                        .size(10)
//                        .sortingCriteria(Map.of("email", "asc"))
//                        .build(), Set.of(Role.ROLE_USER, Role.ROLE_TRAINER)
//        )).withSelfRel());
//        return toCustomModel(pageableResponse, links);
//    }


}
