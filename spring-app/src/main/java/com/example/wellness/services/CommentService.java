package com.example.wellness.services;


import com.example.wellness.dto.comment.CommentBody;
import com.example.wellness.dto.comment.CommentResponse;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.mappers.CommentMapper;
import com.example.wellness.models.Comment;
import com.example.wellness.repositories.CommentRepository;
import com.example.wellness.services.generics.TitleBodyService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentService extends TitleBodyService<Comment, CommentBody, CommentResponse, CommentRepository, CommentMapper> {
    Mono<CommentResponse> createModel(Long postId, CommentBody commentBody);

    Flux<PageableResponse<CommentResponse>> getCommentsByPost(Long postId, PageableBody pageableBody);

    Flux<PageableResponse<ResponseWithUserDto<CommentResponse>>> getCommentsWithUserByPost(Long postId, PageableBody pageableBody);

    Flux<ResponseWithUserDto<CommentResponse>> getCommentsByPost(Long postId);

    Flux<PageableResponse<CommentResponse>> getModelByUser(Long userId, PageableBody pageableBody);

    Mono<Void> deleteCommentsByPost(Long postId);

}
