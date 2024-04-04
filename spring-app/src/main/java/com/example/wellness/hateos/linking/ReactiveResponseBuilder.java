package com.example.wellness.hateos.linking;


import com.example.wellness.dto.common.UserDto;
import com.example.wellness.dto.common.response.*;
import com.example.wellness.hateos.CustomEntityModel;
import com.example.wellness.hateos.user.UserDtoAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.BiFunction;


@RequiredArgsConstructor
public class ReactiveResponseBuilder<RESPONSE, C> {

    protected final UserDtoAssembler userDtoAssembler;

    protected final ReactiveLinkBuilder<RESPONSE, C> linkBuilder;

    public Mono<CustomEntityModel<RESPONSE>> toModel(RESPONSE response, Class<C> clazz) {
        CustomEntityModel<RESPONSE> model = CustomEntityModel.of(response);
        List<Mono<Link>> links = linkBuilder.createModelLinks(response, clazz)
                .stream().map(WebFluxLinkBuilder.WebFluxLink::toMono).toList();

        return Flux.merge(links)
                .collectList()
                .doOnNext(model::add)
                .thenReturn(model);

    }

    public Mono<ResponseWithUserDtoEntity<RESPONSE>> toModelWithUser(ResponseWithUserDto<RESPONSE> response, Class<C> clazz) {
        return userDtoAssembler.toModel(response.getUser())
                .zipWith(toModel(response.getModel(), clazz))
                .map(tuple -> {
                            ResponseWithUserDtoEntity<RESPONSE> entity = new ResponseWithUserDtoEntity<>();
                            entity.setUser(tuple.getT1());
                            entity.setModel(tuple.getT2());
                            return entity;
                        }
                );
    }

    public Mono<ResponseWithUserLikesAndDislikesEntity<RESPONSE>> toModelWithUserLikesAndDislikes(ResponseWithUserLikesAndDislikes<RESPONSE> response, Class<C> clazz) {
        Mono<List<CustomEntityModel<UserDto>>> userLikesListMono = Flux.fromIterable(response.getUserLikes())
                .flatMap(userDtoAssembler::toModel)
                .collectList();

        Mono<List<CustomEntityModel<UserDto>>> userDislikesListMono = Flux.fromIterable(response.getUserDislikes())
                .flatMap(userDtoAssembler::toModel)
                .collectList();

        Mono<ResponseWithUserDtoEntity<RESPONSE>> modelWithUserMono = toModelWithUser(response, clazz);


        return Mono.zip(userLikesListMono, userDislikesListMono, modelWithUserMono)
                .map(tuple -> {
                    ResponseWithUserLikesAndDislikesEntity<RESPONSE> entity = new ResponseWithUserLikesAndDislikesEntity<>();
                    entity.setUserLikes(tuple.getT1());
                    entity.setUserDislikes(tuple.getT2());
                    ResponseWithUserDtoEntity<RESPONSE> userDtoEntity = tuple.getT3();
                    entity.setModel(userDtoEntity.getModel());
                    entity.setUser(userDtoEntity.getUser());
                    return entity;
                });
    }

    public Mono<PageableResponse<CustomEntityModel<RESPONSE>>> toModelPageable(PageableResponse<RESPONSE> pageableResponse, Class<C> clazz) {
        return toModelGeneric(pageableResponse, clazz, this::toModel);
    }

    public Mono<PageableResponse<ResponseWithUserDtoEntity<RESPONSE>>> toModelWithUserPageable(PageableResponse<ResponseWithUserDto<RESPONSE>> response, Class<C> clazz) {
        return toModelGeneric(response, clazz, this::toModelWithUser);
    }

    public Mono<PageableResponse<ResponseWithUserLikesAndDislikesEntity<RESPONSE>>> toModelWithUserLikesAndDislikesPageable(PageableResponse<ResponseWithUserLikesAndDislikes<RESPONSE>> response, Class<C> clazz) {
        return toModelGeneric(response, clazz, this::toModelWithUserLikesAndDislikes);
    }


    private <T, R> Mono<PageableResponse<R>> toModelGeneric(
            PageableResponse<T> response,
            Class<C> clazz,
            BiFunction<T, Class<C>, Mono<R>> conversionFunction) {

        return conversionFunction.apply(response.getContent(), clazz)
                .map(c -> PageableResponse.<R>builder()
                        .content(c)
                        .pageInfo(response.getPageInfo())
                        .build());
    }


}
