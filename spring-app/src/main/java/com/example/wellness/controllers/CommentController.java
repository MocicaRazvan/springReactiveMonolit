package com.example.wellness.controllers;


import com.example.wellness.controllers.generics.TitleBodyController;
import com.example.wellness.dto.comment.CommentBody;
import com.example.wellness.dto.comment.CommentResponse;
import com.example.wellness.dto.common.*;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.response.ResponseWithUserDtoEntity;
import com.example.wellness.dto.common.response.ResponseWithUserLikesAndDislikesEntity;
import com.example.wellness.hateos.CustomEntityModel;
import com.example.wellness.hateos.linking.impl.responses.CommentReactiveResponseBuilder;
import com.example.wellness.mappers.CommentMapper;
import com.example.wellness.models.Comment;
import com.example.wellness.repositories.CommentRepository;
import com.example.wellness.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController implements TitleBodyController<Comment, CommentBody, CommentResponse, CommentRepository,
        CommentMapper, CommentService> {

    private final CommentService commentService;
    private final CommentReactiveResponseBuilder commentReactiveResponseBuilder;

    @Override
    @DeleteMapping(value = "/delete/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<CommentResponse>>> deleteModel(@PathVariable Long id) {
        return commentService.deleteModel(id)
                .flatMap(m -> commentReactiveResponseBuilder.toModel(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<CommentResponse>>> getModelById(@PathVariable Long id) {
        return commentService.getModelById(id)
                .flatMap(m -> commentReactiveResponseBuilder.toModel(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserDtoEntity<CommentResponse>>> getModelByIdWithUser(@PathVariable Long id) {
        return commentService.getModelByIdWithUser(id)
                .flatMap(m -> commentReactiveResponseBuilder.toModelWithUser(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PutMapping(value = "/update/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<CommentResponse>>> updateModel(@Valid @RequestBody CommentBody commentBody, @PathVariable Long id) {
        return commentService.updateModel(id, commentBody)
                .flatMap(m -> commentReactiveResponseBuilder.toModel(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/like/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<CommentResponse>>> likeModel(@PathVariable Long id) {
        return commentService.reactToModel(id, "like")
                .flatMap(m -> commentReactiveResponseBuilder.toModel(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/dislike/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<CommentResponse>>> dislikeModel(@PathVariable Long id) {
        return commentService.reactToModel(id, "dislike")
                .flatMap(m -> commentReactiveResponseBuilder.toModel(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/withReactions/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserLikesAndDislikesEntity<CommentResponse>>> getModelsWithUserAndReaction(@PathVariable Long id) {
        return commentService.getModelByIdWithUserLikesAndDislikes(id)
                .flatMap(m -> commentReactiveResponseBuilder.toModelWithUserLikesAndDislikes(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/create/{postId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<CommentResponse>>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentBody commentBody
    ) {
        return commentService.createModel(postId, commentBody)
                .flatMap(m -> commentReactiveResponseBuilder.toModel(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @PatchMapping(value = "/{postId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<CommentResponse>>> getCommentsByPost(
            @PathVariable Long postId,
            @Valid @RequestBody PageableBody pageableBody

    ) {
        return commentService.getCommentsByPost(postId, pageableBody)
                .flatMap(m -> commentReactiveResponseBuilder.toModelPageable(m, CommentController.class));
    }

    @PatchMapping(value = "/user/{userId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<CommentResponse>>> getCommentsByUser(
            @PathVariable Long userId,
            @Valid @RequestBody PageableBody pageableBody

    ) {
        return commentService.getModelByUser(userId, pageableBody)
                .flatMap(m -> commentReactiveResponseBuilder.toModelPageable(m, CommentController.class));
    }

    @PatchMapping(value = "/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<CommentResponse>>> getModelsByIdIn(@Valid @RequestBody PageableBody pageableBody,
                                                                                      @RequestParam List<Long> ids) {
        return commentService.getModelsByIdIn(ids, pageableBody)
                .flatMap(m -> commentReactiveResponseBuilder.toModelPageable(m, CommentController.class));
    }

    @PatchMapping(value = "/withUser/byPost/{postId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<ResponseWithUserDtoEntity<CommentResponse>>> getCommentsWithUserByPost(
            @PathVariable Long postId,
            @Valid @RequestBody PageableBody pageableBody
    ) {
        return commentService.getCommentsWithUserByPost(postId, pageableBody)
                .flatMap(m -> commentReactiveResponseBuilder.toModelWithUserPageable(m, CommentController.class));
    }
}
