package com.example.wellness.services.impl.generics;

import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.dto.common.response.ResponseWithUserLikesAndDislikes;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.generic.TitleBody;
import com.example.wellness.dto.common.generic.WithUser;
import com.example.wellness.enums.Role;
import com.example.wellness.exceptions.action.IllegalActionException;
import com.example.wellness.exceptions.action.PrivateRouteException;
import com.example.wellness.mappers.UserMapper;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.generic.Approve;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.repositories.generic.ApprovedRepository;
import com.example.wellness.services.generics.ApprovedService;
import com.example.wellness.utils.EntitiesUtils;
import com.example.wellness.utils.PageableUtilsCustom;
import com.example.wellness.utils.UserUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public abstract class ApprovedServiceImpl<MODEL extends Approve, BODY extends TitleBody, RESPONSE extends WithUser,
        S extends ApprovedRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>>
        extends TitleBodyServiceImpl<MODEL, BODY, RESPONSE, S, M>
        implements ApprovedService<MODEL, BODY, RESPONSE, S, M> {


    public ApprovedServiceImpl(S modelRepository, M modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, UserRepository userRepository, UserMapper userMapper, String modelName, List<String> allowedSortingFields, EntitiesUtils entitiesUtils) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, userRepository, userMapper, modelName, allowedSortingFields, entitiesUtils);
    }

    @Override
    public Mono<RESPONSE> approveModel(Long id) {
        return getModel(id)
                .flatMap(model -> {
                    if (model.isApproved()) {
                        return Mono.error(new IllegalActionException(modelName + " with id " + id + " is already approved!"));
                    }
                    model.setApproved(true);
                    return modelRepository.save(model);
                })
                .map(modelMapper::fromModelToResponse);

    }

    @Override
    public Flux<PageableResponse<RESPONSE>> getModelsApproved(PageableBody pageableBody) {
//        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
//                .then(pageableUtils.createPageRequest(pageableBody))
//                .flatMapMany(pr -> pageableUtils.createPageableResponse(
//                                modelRepository.findAllByApproved(true, pr).map(modelMapper::fromModelToResponse),
//                                modelRepository.countByApproved(true),
//                                pr
//                        )
//
//                );
        return getModelsTitle(null, true, pageableBody);
    }

    @Override
    public Flux<PageableResponse<RESPONSE>> getModelsApproved(String title, PageableBody pageableBody) {
        return getModelsTitle(title, true, pageableBody);
    }

    @Override
    public Flux<PageableResponse<RESPONSE>> getAllModels(String title, PageableBody pageableBody) {
        final String newTitle = title == null ? "" : title.trim();
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(pageableUtils.createPageRequest(pageableBody))
                .flatMapMany(pr -> pageableUtils.createPageableResponse(
                                modelRepository.findAllByTitleContainingIgnoreCase(newTitle, pr).map(modelMapper::fromModelToResponse),
                                modelRepository.countAllByTitleContainingIgnoreCase(newTitle),
                                pr
                        )
                );
    }

    @Override
    public Mono<RESPONSE> createModel(BODY body) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> {
                    MODEL model = modelMapper.fromBodyToModel(body);
                    model.setUserId(authUser.getId());
                    model.setApproved(false);
                    model.setUserDislikes(new ArrayList<>());
                    model.setUserLikes(new ArrayList<>());
                    model.setImages(body.getImages());
                    return modelRepository.save(model);
                }).map(modelMapper::fromModelToResponse);
    }

    @Override
    public Mono<RESPONSE> getModelById(Long id) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getResponseGuard(authUser, model, !model.isApproved()))
                );
    }

    @Override
    public Mono<ResponseWithUserDto<RESPONSE>> getModelByIdWithUser(Long id) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getModelGuardWithUser(authUser, model, !model.isApproved()))
                );

    }

    public Flux<PageableResponse<RESPONSE>> getModelsTitle(String title, boolean approved, PageableBody pageableBody) {

        final String newTitle = title == null ? "" : title.trim();

        return
                userUtils.getPrincipal().flatMap(
                                u -> {
                                    if (!u.getRole().equals(Role.ROLE_ADMIN) && !approved) {
                                        return Mono.error(new PrivateRouteException());
                                    }
                                    return Mono.just(u);
                                }
                        )
                        .then(pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields))
                        .then(pageableUtils.createPageRequest(pageableBody))
                        .flatMapMany(pr -> pageableUtils.createPageableResponse(
                                modelRepository.findAllByTitleContainingIgnoreCaseAndApproved(newTitle, approved, pr).map(modelMapper::fromModelToResponse),
                                modelRepository.countAllByTitleContainingIgnoreCaseAndApproved(newTitle, approved),
                                pr
                        ));
    }

    @Override
    public Mono<ResponseWithUserLikesAndDislikes<RESPONSE>> getModelByIdWithUserLikesAndDislikes(Long id) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getModelGuardWithLikesAndDislikes(authUser, model, !model.isApproved()))
                );
    }


    @Override
    public Flux<PageableResponse<RESPONSE>> getModelsTrainer(String title, Long trainerId, PageableBody pageableBody) {
        String newTitle = title == null ? "" : title.trim();
        return userUtils.existsTrainerOrAdmin(trainerId)
                .then(pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields))
                .then(pageableUtils.createPageRequest(pageableBody))
                .flatMapMany(pr -> userUtils.getPrincipal()
                        .flatMapMany(authUser -> privateRoute(true, authUser, trainerId))
                        .thenMany(pageableUtils.createPageableResponse(
                                modelRepository.findAllByUserIdAndTitleContainingIgnoreCase(trainerId, newTitle, pr).map(modelMapper::fromModelToResponse),
                                modelRepository.countAllByUserIdAndTitleContainingIgnoreCase(trainerId, newTitle),
                                pr
                        )));
    }
}
