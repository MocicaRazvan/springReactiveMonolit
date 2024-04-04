package com.example.wellness.repositories.generic;

import com.example.wellness.models.generic.ManyToOneUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@NoRepositoryBean
public interface ManyToOneUserRepository<M extends ManyToOneUser> extends R2dbcRepository<M, Long> {
    Flux<M> findAllByUserId(Long userId, PageRequest pageRequest);

    Flux<M> findAllBy(PageRequest pageRequest);

    Flux<M> findAllByIdIn(List<Long> ids, PageRequest pageRequest);

    Mono<Long> countAllByIdIn(List<Long> ids);

}
