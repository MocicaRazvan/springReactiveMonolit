package com.example.wellness.services;

import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.dto.exercise.*;
import com.example.wellness.mappers.ExerciseMapper;
import com.example.wellness.models.Exercise;
import com.example.wellness.repositories.ExerciseRepository;
import com.example.wellness.services.generics.ApprovedService;
import com.example.wellness.services.generics.ValidIds;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ExerciseService extends ApprovedService
        <Exercise, ExerciseBody, ExerciseResponse, ExerciseRepository, ExerciseMapper>, ValidIds {

    Flux<ResponseWithUserDto<ExerciseResponse>> getExercisesWithUserByIds(List<Long> ids);

    Mono<Void> validIds(List<Long> ids);

    Mono<ExerciseTrainingCount> getTrainingCount(Long id);

    Mono<ExerciseResponseWithTrainingCount> getExerciseWithTrainingCount(Long id);

    Flux<ExerciseResponse> getApprovedModelsTrainer(Long trainerId);

}
