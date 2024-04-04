package com.example.wellness.services.impl.generics;

import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.generic.WithUser;
import com.example.wellness.exceptions.action.PrivateRouteException;
import com.example.wellness.exceptions.notFound.NotFoundEntity;
import com.example.wellness.mappers.UserMapper;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.generic.ManyToOneUser;
import com.example.wellness.models.user.UserCustom;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.repositories.generic.ManyToOneUserRepository;
import com.example.wellness.services.generics.ManyToOneUserService;
import com.example.wellness.utils.PageableUtilsCustom;
import com.example.wellness.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public abstract class ManyToOneUserServiceImpl<MODEL extends ManyToOneUser, BODY, RESPONSE extends WithUser,
        S extends ManyToOneUserRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>> implements ManyToOneUserService<MODEL, BODY, RESPONSE, S, M> {

    protected final S modelRepository;
    protected final M modelMapper;
    protected final PageableUtilsCustom pageableUtils;
    protected final UserUtils userUtils;
    protected final UserRepository userRepository;
    protected final UserMapper userMapper;
    protected final String modelName;
    protected final List<String> allowedSortingFields;


    @Override
    public Mono<RESPONSE> deleteModel(Long id) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> privateRoute(true, authUser, model.getUserId())
                                .then(modelRepository.delete(model))
                                .then(Mono.fromCallable(() -> modelMapper.fromModelToResponse(model)))
                        )
                );
    }

    @Override
    public Mono<RESPONSE> getModelById(Long id) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getResponseGuard(authUser, model, true))
                );
    }

    public Mono<RESPONSE> getResponseGuard(UserCustom authUser, MODEL model, boolean guard) {
        return privateRoute(guard, authUser, model.getUserId())
                .thenReturn(modelMapper.fromModelToResponse(model));
    }

    @Override
    public Flux<PageableResponse<RESPONSE>> getAllModels(PageableBody pageableBody) {
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(pageableUtils.createPageRequest(pageableBody))
                .flatMapMany(pr -> pageableUtils.createPageableResponse(
                                modelRepository.findAllBy(pr).map(modelMapper::fromModelToResponse),
                                modelRepository.count(),
                                pr
                        )

                );
    }

    @Override
    public Mono<RESPONSE> updateModel(Long id, BODY body) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> isNotAuthor(model, authUser)
                                .flatMap(isNotAuthor -> {
                                    if (isNotAuthor) {
                                        return Mono.error(new PrivateRouteException());
                                    } else {
                                        return modelMapper.updateModelFromBody(body, model)
                                                .flatMap(modelRepository::save)
                                                .map(modelMapper::fromModelToResponse);
                                    }
                                })
                        )
                );
    }

    @Override
    public Mono<ResponseWithUserDto<RESPONSE>> getModelByIdWithUser(Long id) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getModelGuardWithUser(authUser, model, true)
                        )
                );

    }

    @Override
    public Flux<ResponseWithUserDto<RESPONSE>> getModelsWithUser(List<Long> ids) {
        return userUtils.getPrincipal()
                .flatMapMany(authUser -> modelRepository.findAllById(ids)
                        .flatMap(model -> getModelGuardWithUser(authUser, model, false))
                );
    }


    public Mono<ResponseWithUserDto<RESPONSE>> getModelGuardWithUser(UserCustom authUser, MODEL model, boolean guard) {
        return privateRoute(guard, authUser, model.getUserId())
                .then(userUtils.getUser(model.getUserId())
                        .map(user ->
                                ResponseWithUserDto.<RESPONSE>builder()
                                        .model(modelMapper.fromModelToResponse(model))
                                        .user(userMapper.fromUserCustomToUserDto(user))
                                        .build()
                        )

                );
    }


    public Mono<MODEL> getModel(Long id) {
        return modelRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundEntity(modelName, id)));
    }

    public Mono<Boolean> isNotAuthor(MODEL model, UserCustom authUser) {
        return Mono.just(
                !model.getUserId().equals(authUser.getId())
        );
    }

    public Mono<Void> privateRoute(boolean guard, UserCustom authUser, Long ownerId) {
        return userUtils.hasPermissionToModifyEntity(authUser, ownerId)
                .flatMap(perm -> {
                    if (guard && !perm) {
                        return Mono.error(new PrivateRouteException());
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Flux<PageableResponse<RESPONSE>> getModelsByIdIn(List<Long> ids, PageableBody pageableBody) {
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(pageableUtils.createPageRequest(pageableBody))
                .flatMapMany(pr -> pageableUtils.createPageableResponse(
                                modelRepository.findAllByIdIn(ids, pr).map(modelMapper::fromModelToResponse),
                                modelRepository.countAllByIdIn(ids),
                                pr
                        )

                );
    }
}
