package com.example.wellness.services.impl.generics;

import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.dto.common.response.ResponseWithUserLikesAndDislikes;
import com.example.wellness.dto.common.UserDto;
import com.example.wellness.dto.common.generic.WithUser;
import com.example.wellness.mappers.UserMapper;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.generic.TitleBody;
import com.example.wellness.models.user.UserCustom;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.repositories.generic.TitleBodyRepository;
import com.example.wellness.services.generics.TitleBodyService;
import com.example.wellness.utils.EntitiesUtils;
import com.example.wellness.utils.PageableUtilsCustom;
import com.example.wellness.utils.UserUtils;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class TitleBodyServiceImpl<MODEL extends TitleBody, BODY, RESPONSE extends WithUser,
        S extends TitleBodyRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>>
        extends ManyToOneUserServiceImpl<MODEL, BODY, RESPONSE, S, M>
        implements TitleBodyService<MODEL, BODY, RESPONSE, S, M> {


    public TitleBodyServiceImpl(S modelRepository, M modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, UserRepository userRepository, UserMapper userMapper, String modelName, List<String> allowedSortingFields, EntitiesUtils entitiesUtils) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, userRepository, userMapper, modelName, allowedSortingFields);
        this.entitiesUtils = entitiesUtils;
    }

    protected final EntitiesUtils entitiesUtils;

    @Override
    public Mono<RESPONSE> reactToModel(Long id, String type) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> entitiesUtils.setReaction(model, authUser, type)
                                .flatMap(modelRepository::save)
                                .map(modelMapper::fromModelToResponse)
                        )


                );
    }

    @Override
    public Mono<ResponseWithUserLikesAndDislikes<RESPONSE>> getModelByIdWithUserLikesAndDislikes(Long id) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getModelGuardWithLikesAndDislikes(authUser, model, true))
                );
    }

    public Mono<ResponseWithUserLikesAndDislikes<RESPONSE>> getModelGuardWithLikesAndDislikes(UserCustom authUser, MODEL model, boolean guard) {
        return getModelGuardWithUser(authUser, model, guard)
                .zipWith(userRepository.findAllById(model.getUserLikes()).map(userMapper::fromUserCustomToUserDto).collectList())
                .zipWith(userRepository.findAllById(model.getUserDislikes()).map(userMapper::fromUserCustomToUserDto).collectList())
                .map(tuple -> {
                    ResponseWithUserDto<RESPONSE> responseWithUserDto = tuple.getT1().getT1();
                    List<UserDto> userLikes = tuple.getT1().getT2();
                    List<UserDto> userDislikes = tuple.getT2();
                    return ResponseWithUserLikesAndDislikes.<RESPONSE>builder()
                            .model(responseWithUserDto.getModel())
                            .user(responseWithUserDto.getUser())
                            .userLikes(userLikes)
                            .userDislikes(userDislikes)
                            .build();
                });
    }


}
