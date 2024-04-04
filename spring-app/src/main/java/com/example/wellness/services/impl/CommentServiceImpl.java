package com.example.wellness.services.impl;

import com.example.wellness.dto.comment.CommentBody;
import com.example.wellness.dto.comment.CommentResponse;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.enums.Role;
import com.example.wellness.exceptions.action.PrivateRouteException;
import com.example.wellness.exceptions.notFound.NotFoundEntity;
import com.example.wellness.mappers.CommentMapper;
import com.example.wellness.mappers.UserMapper;
import com.example.wellness.models.Comment;
import com.example.wellness.repositories.CommentRepository;
import com.example.wellness.repositories.PostRepository;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.services.CommentService;
import com.example.wellness.services.impl.generics.TitleBodyServiceImpl;
import com.example.wellness.utils.EntitiesUtils;
import com.example.wellness.utils.PageableUtilsCustom;
import com.example.wellness.utils.UserUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


@Service
public class CommentServiceImpl extends TitleBodyServiceImpl<Comment, CommentBody, CommentResponse, CommentRepository, CommentMapper>
        implements CommentService {
    private final PostRepository postRepository;

    public CommentServiceImpl(CommentRepository modelRepository, CommentMapper modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, UserRepository userRepository, UserMapper userMapper, EntitiesUtils entitiesUtils, PostRepository postRepository) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, userRepository, userMapper, "comment", List.of("id", "userId", "postId", "title", "createdAt"), entitiesUtils);
        this.postRepository = postRepository;
    }


    @Override
    public Mono<CommentResponse> createModel(Long postId, CommentBody commentBody) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> entitiesUtils.getEntityById(postId, "post", postRepository)
                        .flatMap(post -> {
                            Comment comment = modelMapper.fromBodyToModel(commentBody);
                            comment.setPostId(postId);
                            comment.setUserId(authUser.getId());
                            comment.setUserDislikes(new ArrayList<>());
                            comment.setUserLikes(new ArrayList<>());
                            return modelRepository.save(comment)
                                    .map(modelMapper::fromModelToResponse);
                        })
                );
    }

    @Override
    public Flux<PageableResponse<CommentResponse>> getCommentsByPost(Long postId, PageableBody pageableBody) {
        return getPost(postId, pageableBody, postRepository::existsByIdAndApprovedIsTrue)
                .thenMany(pageableUtils.createPageRequest(pageableBody)
                        .flatMapMany(pr ->
                                pageableUtils.createPageableResponse(
                                        modelRepository.findAllByPostId(postId, pr).map(modelMapper::fromModelToResponse),
                                        modelRepository.countAllByPostId(postId),
                                        pr
                                )))

                ;
    }

    private Mono<Boolean> getPost(Long postId, PageableBody pageableBody, Function<Long, Mono<Boolean>> func) {
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(func.apply(postId)
                        .filter(Boolean::booleanValue)
                        .switchIfEmpty(Mono.error(new NotFoundEntity("post", postId))));
    }

    @Override
    public Flux<PageableResponse<ResponseWithUserDto<CommentResponse>>> getCommentsWithUserByPost(Long postId, PageableBody pageableBody) {
        return getPost(postId, pageableBody, postRepository::existsById)
                .thenMany(pageableUtils.createPageRequest(pageableBody)
                        .flatMapMany(pr ->
                                pageableUtils.createPageableResponse(
                                        modelRepository.findAllByPostId(postId, pr).flatMap(c ->
                                                userUtils.getUser(c.getUserId())
                                                        .map(user -> ResponseWithUserDto.<CommentResponse>builder()
                                                                .user(userMapper.fromUserCustomToUserDto(user))
                                                                .model(modelMapper.fromModelToResponse(c))
                                                                .build()
                                                        )
                                        ),
                                        modelRepository.countAllByPostId(postId),
                                        pr
                                ))

                );
    }

    @Override
    public Flux<ResponseWithUserDto<CommentResponse>> getCommentsByPost(Long postId) {
        return postRepository.existsByIdAndApprovedIsTrue(postId)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new NotFoundEntity("post", postId)))
                .thenMany(modelRepository.findAllByPostId(postId).flatMap(c ->
                        userUtils.getUser(c.getUserId())
                                .map(user -> ResponseWithUserDto.<CommentResponse>builder()
                                        .user(userMapper.fromUserCustomToUserDto(user))
                                        .model(modelMapper.fromModelToResponse(c))
                                        .build()
                                ))
                );
    }


    @Override
    public Flux<PageableResponse<CommentResponse>> getModelByUser(Long userId, PageableBody pageableBody) {
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(userUtils.getUser(userId))
                .thenMany(pageableUtils.createPageRequest(pageableBody)
                        .flatMapMany(pr -> pageableUtils.createPageableResponse(
                                modelRepository.findAllByUserId(userId, pr).map(modelMapper::fromModelToResponse),
                                modelRepository.countAllByUserId(userId),
                                pr
                        )));
    }

    @Override
    public Mono<Void> deleteCommentsByPost(Long postId) {
        return modelRepository.deleteAllByPostId(postId);
    }


    @Override
    public Mono<CommentResponse> deleteModel(Long id) {
        return userUtils.getPrincipal()
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> isNotAuthor(model, authUser)
                                .map(notAuthor -> {
                                    if (notAuthor && authUser.getRole() == Role.ROLE_ADMIN) {
                                        return Mono.error(new PrivateRouteException());
                                    }
                                    return Mono.empty();
                                })
                                .then(modelRepository.delete(model))
                                .thenReturn(modelMapper.fromModelToResponse(model))
                        )
                );
    }


}
