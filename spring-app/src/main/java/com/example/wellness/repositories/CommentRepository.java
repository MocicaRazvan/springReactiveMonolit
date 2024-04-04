package com.example.wellness.repositories;

import com.example.wellness.models.Comment;
import com.example.wellness.repositories.generic.TitleBodyRepository;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepository extends TitleBodyRepository<Comment> {

    Flux<Comment> findAllByPostId(Long postId, PageRequest pageRequest);

    Mono<Long> countAllByPostId(Long postId);

    Mono<Long> countAllByUserId(Long userId);

    Flux<Comment> findAllByPostId(Long postId);

    Mono<Void> deleteAllByPostId(Long postId);
}
