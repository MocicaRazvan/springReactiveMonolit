package com.example.wellness.controllers;


import com.example.wellness.controllers.generics.ApproveController;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.*;
import com.example.wellness.dto.exercise.ExerciseResponse;
import com.example.wellness.dto.training.TrainingBody;
import com.example.wellness.dto.training.TrainingResponse;
import com.example.wellness.dto.training.TrainingResponseWithOrderCount;
import com.example.wellness.hateos.CustomEntityModel;
import com.example.wellness.hateos.linking.impl.responses.ExerciseReactiveResponseBuilder;
import com.example.wellness.hateos.linking.impl.responses.TrainingReactiveResponseBuilder;
import com.example.wellness.mappers.TrainingMapper;
import com.example.wellness.models.Training;
import com.example.wellness.repositories.TrainingRepository;
import com.example.wellness.services.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
public class TrainingController implements ApproveController
        <Training, TrainingBody, TrainingResponse, TrainingRepository, TrainingMapper,
                TrainingService> {

    private final TrainingService trainingService;
    private final TrainingReactiveResponseBuilder trainingReactiveResponseBuilder;
    private final ExerciseReactiveResponseBuilder exerciseReactiveResponseBuilder;

    @Override
    @PatchMapping(value = "/approved", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<TrainingResponse>>> getModelsApproved(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody) {
        return trainingService.getModelsApproved(title, pageableBody)
//                .delayElements(Duration.ofSeconds(2)) // for testing
                .flatMap(m -> trainingReactiveResponseBuilder.toModelPageable(m, TrainingController.class));
    }

    @Override
    @PatchMapping(value = "/trainer/{trainerId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<TrainingResponse>>> getModelsTrainer(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, @PathVariable Long trainerId) {
        return trainingService.getModelsTrainer(title, trainerId, pageableBody)
                .flatMap(m -> trainingReactiveResponseBuilder.toModelPageable(m, TrainingController.class));
    }

    @Override
    @PostMapping(value = "/create", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> createModel(@Valid @RequestBody TrainingBody body) {
        return trainingService.createModel(body)
                .flatMap(t -> trainingReactiveResponseBuilder.toModel(t, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/admin/approve/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> approveModel(@PathVariable Long id) {
        return trainingService.approveModel(id)
                .flatMap(t -> trainingReactiveResponseBuilder.toModel(t, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/admin", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<PageableResponse<CustomEntityModel<TrainingResponse>>> getAllModelsAdmin(@RequestParam(required = false) String title, @Valid PageableBody pageableBody) {
        return trainingService.getAllModels(title, pageableBody)
                .flatMap(m -> trainingReactiveResponseBuilder.toModelPageable(m, TrainingController.class));
    }

    @Override
    @DeleteMapping(value = "/delete/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> deleteModel(@PathVariable Long id) {
        return trainingService.deleteModel(id)
                .flatMap(t -> trainingReactiveResponseBuilder.toModel(t, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> getModelById(@PathVariable Long id) {
        return trainingService.getModelById(id)
                .flatMap(t -> trainingReactiveResponseBuilder.toModel(t, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserDtoEntity<TrainingResponse>>> getModelByIdWithUser(@PathVariable Long id) {
        return trainingService.getModelByIdWithUser(id)
                .flatMap(t -> trainingReactiveResponseBuilder.toModelWithUser(t, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PutMapping(value = "/update/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> updateModel(@Valid @RequestBody TrainingBody trainingBody, @PathVariable Long id) {
        return trainingService.updateModel(id, trainingBody)
                .flatMap(t -> trainingReactiveResponseBuilder.toModel(t, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<PageableResponse<CustomEntityModel<TrainingResponse>>> getModelsByIdIn(@Valid @RequestBody PageableBody pageableBody, @RequestParam List<Long> ids) {
        return trainingService.getModelsByIdIn(ids, pageableBody)
                .flatMap(m -> trainingReactiveResponseBuilder.toModelPageable(m, TrainingController.class));
    }

    @Override
    @PatchMapping(value = "/like/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> likeModel(@PathVariable Long id) {
        return trainingService.reactToModel(id, "like")
                .flatMap(t -> trainingReactiveResponseBuilder.toModel(t, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/dislike/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> dislikeModel(@PathVariable Long id) {
        return trainingService.reactToModel(id, "dislike")
                .flatMap(t -> trainingReactiveResponseBuilder.toModel(t, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/withReactions/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserLikesAndDislikesEntity<TrainingResponse>>> getModelsWithUserAndReaction(@PathVariable Long id) {
        return trainingService.getModelByIdWithUserLikesAndDislikes(id)
                .flatMap(t -> trainingReactiveResponseBuilder.toModelWithUserLikesAndDislikes(t, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/withExercises", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<ResponseWithChildListEntity<TrainingResponse, ResponseWithUserDtoEntity<ExerciseResponse>>> getTrainingsWithExercises(@RequestParam List<Long> ids) {
        return trainingService.getTrainingsWithExercises(ids, true)
                .flatMap(this::getResponseWithChildListEntityMono);
    }

    @GetMapping(value = "/withExercises/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithChildListEntity<TrainingResponse, ResponseWithUserDtoEntity<ExerciseResponse>>>> getTrainingWithExercises(@PathVariable Long id) {
        return trainingService.getTrainingWithExercises(id, true)
                .flatMap(m ->
                        getResponseWithChildListEntityMono(m).map(ResponseEntity::ok)
                );
    }

    @GetMapping(value = "/withOrderCount/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<TrainingResponseWithOrderCount>> getTrainingWithOrderCount(@PathVariable Long id) {
        return trainingService.getTrainingWithOrderCount(id)
                .map(ResponseEntity::ok);
    }

    public Mono<ResponseWithChildListEntity<TrainingResponse, ResponseWithUserDtoEntity<ExerciseResponse>>> getResponseWithChildListEntityMono(ResponseWithChildList<TrainingResponse, ResponseWithUserDto<ExerciseResponse>> m) {
        return Flux.fromIterable(m.getChildren())
                .flatMap(c -> exerciseReactiveResponseBuilder.toModelWithUser(c, ExerciseController.class))
                .collectList()
                .flatMap(commentResponses ->
                        trainingReactiveResponseBuilder.toModel(m.getEntity(), TrainingController.class)
                                .map(model -> new ResponseWithChildListEntity<>(model, commentResponses))
                );
    }


}
