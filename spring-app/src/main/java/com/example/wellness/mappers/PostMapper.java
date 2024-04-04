package com.example.wellness.mappers;

import com.example.wellness.dto.post.PostBody;
import com.example.wellness.dto.post.PostResponse;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.Post;
import org.mapstruct.Mapper;
import reactor.core.publisher.Mono;


@Mapper(componentModel = "spring")
public abstract class PostMapper extends DtoMapper<Post, PostBody, PostResponse> {

    @Override
    public Mono<Post> updateModelFromBody(PostBody body, Post post) {
        post.setTags(body.getTags());
        post.setTitle(body.getTitle());
        post.setBody(body.getBody());
        post.setApproved(false);
        post.setImages(body.getImages());
        return Mono.just(post);
    }
}
