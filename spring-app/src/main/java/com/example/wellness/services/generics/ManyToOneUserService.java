package com.example.wellness.services.generics;

import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.generic.WithUser;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.generic.ManyToOneUser;
import com.example.wellness.repositories.generic.ManyToOneUserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ManyToOneUserService<MODEL extends ManyToOneUser, BODY, RESPONSE extends WithUser,
        S extends ManyToOneUserRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>> {
    Mono<RESPONSE> deleteModel(Long id);

    Mono<RESPONSE> getModelById(Long id);

    Flux<PageableResponse<RESPONSE>> getAllModels(PageableBody pageableBody);

    Mono<RESPONSE> updateModel(Long id, BODY body);

    Mono<ResponseWithUserDto<RESPONSE>> getModelByIdWithUser(Long id);

    Flux<ResponseWithUserDto<RESPONSE>> getModelsWithUser(List<Long> ids);

    Flux<PageableResponse<RESPONSE>> getModelsByIdIn(List<Long> ids, PageableBody pageableBody);

}
