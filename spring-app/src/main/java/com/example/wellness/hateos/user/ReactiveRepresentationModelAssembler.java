package com.example.wellness.hateos.user;

import com.example.wellness.hateos.CustomEntityModel;
import org.springframework.hateoas.EntityModel;
import reactor.core.publisher.Mono;

public interface ReactiveRepresentationModelAssembler<T> {
//    Mono<EntityModel<T>> toModel(T entity);

    Mono<CustomEntityModel<T>> toModel(T entity);
}
