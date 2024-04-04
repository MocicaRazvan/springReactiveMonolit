package com.example.wellness.services.generics;

import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.response.ResponseWithUserLikesAndDislikes;
import com.example.wellness.dto.common.generic.WithUser;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.generic.TitleBody;
import com.example.wellness.repositories.generic.TitleBodyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TitleBodyService<MODEL extends TitleBody, BODY, RESPONSE extends WithUser,
        S extends TitleBodyRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>>
        extends ManyToOneUserService<MODEL, BODY, RESPONSE, S, M> {

    Mono<RESPONSE> reactToModel(Long id, String type);

    Mono<ResponseWithUserLikesAndDislikes<RESPONSE>> getModelByIdWithUserLikesAndDislikes(Long id);


}
