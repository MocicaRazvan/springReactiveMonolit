package com.example.wellness.mappers;


import com.example.wellness.dto.exercise.ExerciseBody;
import com.example.wellness.dto.exercise.ExerciseResponse;
import com.example.wellness.dto.exercise.ExerciseResponseWithTrainingCount;
import com.example.wellness.dto.exercise.ExerciseWithTrainingCount;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.Exercise;
import org.mapstruct.Mapper;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public abstract class ExerciseMapper extends DtoMapper<Exercise, ExerciseBody, ExerciseResponse> {

    @Override
    public Mono<Exercise> updateModelFromBody(ExerciseBody body, Exercise exercise) {
        exercise.setMuscleGroups(body.getMuscleGroups());
        exercise.setTitle(body.getTitle());
        exercise.setBody(body.getBody());
        exercise.setApproved(false);
        exercise.setImages(body.getImages());
        exercise.setVideos(body.getVideos());
        return Mono.just(exercise);
    }

    public abstract ExerciseResponseWithTrainingCount fromModelToResponseWithTrainingCount(ExerciseWithTrainingCount exerciseWithTrainingCount);

}
