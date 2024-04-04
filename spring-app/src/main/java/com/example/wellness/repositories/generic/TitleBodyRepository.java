package com.example.wellness.repositories.generic;

import com.example.wellness.models.generic.TitleBody;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Flux;

@NoRepositoryBean
public interface TitleBodyRepository<M extends TitleBody> extends ManyToOneUserRepository<M> {
}
