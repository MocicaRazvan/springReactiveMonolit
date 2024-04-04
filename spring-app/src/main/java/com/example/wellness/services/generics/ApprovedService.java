package com.example.wellness.services.generics;

import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.generic.TitleBody;
import com.example.wellness.dto.common.generic.WithUser;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.generic.Approve;
import com.example.wellness.repositories.generic.ApprovedRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApprovedService<MODEL extends Approve, BODY extends TitleBody, RESPONSE extends WithUser,
        S extends ApprovedRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>>
        extends TitleBodyService<MODEL, BODY, RESPONSE, S, M> {
    Mono<RESPONSE> approveModel(Long id);

    Flux<PageableResponse<RESPONSE>> getModelsApproved(PageableBody pageableBody);

    Flux<PageableResponse<RESPONSE>> getModelsApproved(String title, PageableBody pageableBody);

    Mono<RESPONSE> createModel(BODY body);

    Flux<PageableResponse<RESPONSE>> getModelsTrainer(String title, Long trainerId, PageableBody pageableBody);

    Flux<PageableResponse<RESPONSE>> getAllModels(String title, PageableBody pageableBody);

//    Flux<PageableResponse<RESPONSE>> getModelsTitle(String title, boolean approved, PageableBody pageableBody);
}
