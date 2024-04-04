package com.example.wellness.controllers;

import com.example.wellness.controllers.generics.ApproveController;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.response.ResponseWithUserDtoEntity;
import com.example.wellness.dto.common.response.ResponseWithUserLikesAndDislikesEntity;
import com.example.wellness.dto.exercise.ExerciseBody;
import com.example.wellness.dto.exercise.ExerciseResponse;
import com.example.wellness.dto.exercise.ExerciseResponseWithTrainingCount;
import com.example.wellness.dto.exercise.ExerciseTrainingCount;
import com.example.wellness.hateos.CustomEntityModel;
import com.example.wellness.hateos.linking.impl.responses.ExerciseReactiveResponseBuilder;
import com.example.wellness.mappers.ExerciseMapper;
import com.example.wellness.models.Exercise;
import com.example.wellness.repositories.ExerciseRepository;
import com.example.wellness.services.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
@Slf4j
public class ExerciseController implements ApproveController
        <Exercise, ExerciseBody, ExerciseResponse, ExerciseRepository, ExerciseMapper,
                ExerciseService> {

    private final ExerciseService exerciseService;

    private final ExerciseReactiveResponseBuilder exerciseReactiveResponseBuilder;

    @Override
    @PatchMapping(value = "/approved", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<ExerciseResponse>>> getModelsApproved(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody) {
        return exerciseService.getModelsApproved(title, pageableBody)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelPageable(e, ExerciseController.class));
    }

    @Override
    @PatchMapping(value = "/trainer/{trainerId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<ExerciseResponse>>> getModelsTrainer(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, @PathVariable Long trainerId) {
        return exerciseService.getModelsTrainer(title, trainerId, pageableBody)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelPageable(e, ExerciseController.class));
    }

    @Override
    @PostMapping(value = "/create", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> createModel(@Valid @RequestBody ExerciseBody body) {
        return exerciseService.createModel(body)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/admin/approve/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> approveModel(@PathVariable Long id) {
        return exerciseService.approveModel(id)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/admin", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<ExerciseResponse>>> getAllModelsAdmin(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody) {
        return exerciseService.getAllModels(title, pageableBody)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelPageable(e, ExerciseController.class));
    }

    @Override
    @DeleteMapping(value = "/delete/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> deleteModel(@PathVariable Long id) {
        return exerciseService.deleteModel(id)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> getModelById(@PathVariable Long id) {
        return exerciseService.getModelById(id)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserDtoEntity<ExerciseResponse>>> getModelByIdWithUser(@PathVariable Long id) {
        return exerciseService.getModelByIdWithUser(id)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelWithUser(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PutMapping(value = "/update/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> updateModel(@Valid @RequestBody ExerciseBody exerciseBody, @PathVariable Long id) {
        return exerciseService.updateModel(id, exerciseBody)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/like/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> likeModel(@PathVariable Long id) {
        return exerciseService.reactToModel(id, "like")
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/dislike/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> dislikeModel(@PathVariable Long id) {
        return exerciseService.reactToModel(id, "dislike")
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/withReactions/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserLikesAndDislikesEntity<ExerciseResponse>>> getModelsWithUserAndReaction(@PathVariable Long id) {
        return exerciseService.getModelByIdWithUserLikesAndDislikes(id)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelWithUserLikesAndDislikes(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @PatchMapping(value = "/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<ExerciseResponse>>> getModelsByIdIn(@Valid @RequestBody PageableBody pageableBody,
                                                                                       @RequestParam List<Long> ids) {
        log.info("ids: " + ids);

        return exerciseService.getModelsByIdIn(ids, pageableBody)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelPageable(e, ExerciseController.class));
    }

    @GetMapping(value = "/count/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ExerciseTrainingCount>> getTrainingCount(@PathVariable Long id) {
        return exerciseService.getTrainingCount(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/withTrainingCount/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ExerciseResponseWithTrainingCount>> getExerciseWithTrainingCount(@PathVariable Long id) {
        return exerciseService.getExerciseWithTrainingCount(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/approved/trainer/{trainerId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<CustomEntityModel<ExerciseResponse>> getApprovedModelsTrainer(@PathVariable Long trainerId) {
        return exerciseService.getApprovedModelsTrainer(trainerId)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class));
    }
}
