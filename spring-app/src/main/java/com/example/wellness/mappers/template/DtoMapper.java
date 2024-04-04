package com.example.wellness.mappers.template;


import com.example.wellness.dto.common.generic.WithUser;
import com.example.wellness.models.generic.ManyToOneUser;
import reactor.core.publisher.Mono;

public abstract class DtoMapper<MODEL extends ManyToOneUser, BODY, RESPONSE extends WithUser> {

    public abstract RESPONSE fromModelToResponse(MODEL model);

    public abstract MODEL fromBodyToModel(BODY body);

    public abstract Mono<MODEL> updateModelFromBody(BODY body, MODEL model);

}
