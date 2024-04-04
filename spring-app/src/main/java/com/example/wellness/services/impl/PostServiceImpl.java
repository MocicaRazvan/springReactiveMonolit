package com.example.wellness.services.impl;

import com.example.wellness.dto.comment.CommentResponse;
import com.example.wellness.dto.common.response.ResponseWithChildList;
import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.dto.post.PostBody;
import com.example.wellness.dto.post.PostResponse;
import com.example.wellness.exceptions.notFound.NotFoundEntity;
import com.example.wellness.mappers.PostMapper;
import com.example.wellness.mappers.UserMapper;
import com.example.wellness.models.Post;
import com.example.wellness.repositories.PostRepository;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.services.CommentService;
import com.example.wellness.services.PostService;
import com.example.wellness.services.impl.generics.ApprovedServiceImpl;
import com.example.wellness.utils.EntitiesUtils;
import com.example.wellness.utils.PageableUtilsCustom;
import com.example.wellness.utils.UserUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
public class PostServiceImpl extends ApprovedServiceImpl<Post, PostBody, PostResponse, PostRepository, PostMapper>
        implements PostService {

    private final CommentService commentService;

    public PostServiceImpl(PostRepository modelRepository, PostMapper modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, UserRepository userRepository, UserMapper userMapper, EntitiesUtils entitiesUtils, CommentService commentService) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, userRepository, userMapper, "post", List.of("id", "userId", "postId", "title", "createdAt"), entitiesUtils);
        this.commentService = commentService;
    }


    @Override
    public Mono<ResponseWithChildList<PostResponse, ResponseWithUserDto<CommentResponse>>> getPostWithComments(Long id, boolean approved) {
        return modelRepository.findByApprovedAndId(approved, id)
                .switchIfEmpty(Mono.error(new NotFoundEntity("post", id)))
                .flatMap(post -> commentService.getCommentsByPost(post.getId())
                        .collectList()
                        .map(comments -> new ResponseWithChildList<>(modelMapper.fromModelToResponse(post), comments))
                );
    }

    @Override
    public Mono<PostResponse> deleteModel(Long id) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> privateRoute(true, authUser, model.getUserId())
                                .then(commentService.deleteCommentsByPost(id))
                                .then(modelRepository.delete(model))
                                .then(Mono.fromCallable(() -> modelMapper.fromModelToResponse(model)))
                        )
                );
    }
}
