package com.example.wellness.controllers;

import com.example.wellness.controllers.generics.ApproveController;
import com.example.wellness.dto.comment.CommentResponse;
import com.example.wellness.dto.common.*;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.response.ResponseWithChildListEntity;
import com.example.wellness.dto.common.response.ResponseWithUserDtoEntity;
import com.example.wellness.dto.common.response.ResponseWithUserLikesAndDislikesEntity;
import com.example.wellness.dto.post.PostBody;
import com.example.wellness.dto.post.PostResponse;
import com.example.wellness.hateos.CustomEntityModel;
import com.example.wellness.hateos.linking.impl.responses.CommentReactiveResponseBuilder;
import com.example.wellness.hateos.linking.impl.responses.PostReactiveResponseBuilder;
import com.example.wellness.mappers.PostMapper;
import com.example.wellness.models.Post;
import com.example.wellness.repositories.PostRepository;
import com.example.wellness.services.PostService;
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
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController implements ApproveController
        <Post, PostBody, PostResponse,
                PostRepository, PostMapper,
                PostService> {

    private final PostService postService;
    private final PostReactiveResponseBuilder postReactiveResponseBuilder;
    private final CommentReactiveResponseBuilder commentReactiveResponseBuilder;

    @Override
    @PatchMapping(value = "/approved", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<PostResponse>>> getModelsApproved(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody) {
        return postService.getModelsApproved(title, pageableBody)
                .flatMap(m -> postReactiveResponseBuilder.toModelPageable(m, PostController.class));
    }

    @Override
    @PatchMapping(value = "/trainer/{trainerId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<PostResponse>>> getModelsTrainer(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, @PathVariable Long trainerId) {
        return postService.getModelsTrainer(title, trainerId, pageableBody)
                .flatMap(m -> postReactiveResponseBuilder.toModelPageable(m, PostController.class));
    }

    @Override
    @PostMapping(value = "/create", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> createModel(@Valid @RequestBody PostBody body) {
        return postService.createModel(body)
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/admin/approve/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> approveModel(@PathVariable Long id) {
        return postService.approveModel(id)
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/admin", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<PostResponse>>> getAllModelsAdmin(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody) {
        return postService.getAllModels(title, pageableBody)
                .flatMap(m -> postReactiveResponseBuilder.toModelPageable(m, PostController.class));
    }

    @Override
    @DeleteMapping(value = "/delete/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> deleteModel(@PathVariable Long id) {
        return postService.deleteModel(id)
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> getModelById(@PathVariable Long id) {
        return postService.getModelById(id)
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserDtoEntity<PostResponse>>> getModelByIdWithUser(@PathVariable Long id) {
        return postService.getModelByIdWithUser(id)
                .flatMap(m -> postReactiveResponseBuilder.toModelWithUser(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PutMapping(value = "/update/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> updateModel(@Valid @RequestBody PostBody postBody, @PathVariable Long id) {
        return postService.updateModel(id, postBody)
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/like/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> likeModel(@PathVariable Long id) {
        return postService.reactToModel(id, "like")
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/dislike/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> dislikeModel(@PathVariable Long id) {
        return postService.reactToModel(id, "dislike")
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/withReactions/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserLikesAndDislikesEntity<PostResponse>>> getModelsWithUserAndReaction(@PathVariable Long id) {
        return postService.getModelByIdWithUserLikesAndDislikes(id)
                .flatMap(m -> postReactiveResponseBuilder.toModelWithUserLikesAndDislikes(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/withComments/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithChildListEntity<PostResponse, ResponseWithUserDtoEntity<CommentResponse>>>>
    getPostWithComments(@PathVariable Long id) {
        return postService.getPostWithComments(id, true)
                .flatMap(m ->
                        Flux.fromIterable(m.getChildren())
                                .flatMap(c -> commentReactiveResponseBuilder.toModelWithUser(c, CommentController.class))
                                .collectList()
                                .flatMap(commentResponses ->
                                        postReactiveResponseBuilder.toModel(m.getEntity(), PostController.class)
                                                .map(postModel -> ResponseEntity.ok(new ResponseWithChildListEntity<>(postModel, commentResponses)))
                                )
                );

    }

    @GetMapping(value = "/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<PostResponse>>> getModelsByIdIn(@Valid @RequestBody PageableBody pageableBody,
                                                                                   @RequestParam List<Long> ids) {
        return postService.getModelsByIdIn(ids, pageableBody)
                .flatMap(m -> postReactiveResponseBuilder.toModelPageable(m, PostController.class));
    }
}
