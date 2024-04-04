package com.example.wellness.services;


import com.example.wellness.dto.common.response.ResponseWithChildList;
import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.dto.exercise.ExerciseResponse;
import com.example.wellness.dto.training.TrainingBody;
import com.example.wellness.dto.training.TrainingResponse;
import com.example.wellness.dto.training.TrainingResponseWithOrderCount;
import com.example.wellness.mappers.TrainingMapper;
import com.example.wellness.models.Training;
import com.example.wellness.repositories.TrainingRepository;
import com.example.wellness.services.generics.ApprovedService;
import com.example.wellness.services.generics.ValidIds;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TrainingService extends ApprovedService<Training, TrainingBody, TrainingResponse, TrainingRepository, TrainingMapper>, ValidIds {
// trainings with exercises

    Mono<ResponseWithChildList<TrainingResponse, ResponseWithUserDto<ExerciseResponse>>>
    getTrainingWithExercises(Long id, boolean approved);

    Flux<ResponseWithChildList<TrainingResponse, ResponseWithUserDto<ExerciseResponse>>>
    getTrainingsWithExercises(List<Long> ids, boolean approved);

    Mono<Double> getTotalPriceById(List<Long> ids);

    Mono<TrainingResponseWithOrderCount> getTrainingWithOrderCount(Long id);
}
