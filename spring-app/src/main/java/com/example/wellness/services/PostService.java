package com.example.wellness.services;

import com.example.wellness.dto.comment.CommentResponse;
import com.example.wellness.dto.common.response.ResponseWithChildList;
import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.dto.post.PostBody;
import com.example.wellness.dto.post.PostResponse;
import com.example.wellness.mappers.PostMapper;
import com.example.wellness.models.Post;
import com.example.wellness.repositories.PostRepository;
import com.example.wellness.services.generics.ApprovedService;
import reactor.core.publisher.Mono;


public interface PostService extends ApprovedService<Post, PostBody, PostResponse, PostRepository, PostMapper> {
    // post with comments
    Mono<ResponseWithChildList<PostResponse, ResponseWithUserDto<CommentResponse>>>
    getPostWithComments(Long id, boolean approved);
}
