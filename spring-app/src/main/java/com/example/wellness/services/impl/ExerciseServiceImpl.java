package com.example.wellness.services.impl;

import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.dto.exercise.*;
import com.example.wellness.exceptions.action.SubEntityUsed;
import com.example.wellness.exceptions.notFound.NotFoundEntity;
import com.example.wellness.mappers.ExerciseMapper;
import com.example.wellness.mappers.UserMapper;
import com.example.wellness.models.Exercise;
import com.example.wellness.repositories.ExerciseRepository;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.services.ExerciseService;
import com.example.wellness.services.generics.ValidIds;
import com.example.wellness.services.impl.generics.ApprovedServiceImpl;
import com.example.wellness.utils.EntitiesUtils;
import com.example.wellness.utils.PageableUtilsCustom;
import com.example.wellness.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class ExerciseServiceImpl
        extends ApprovedServiceImpl<Exercise, ExerciseBody, ExerciseResponse, ExerciseRepository, ExerciseMapper>
        implements ExerciseService, ValidIds {


    public ExerciseServiceImpl(ExerciseRepository modelRepository, ExerciseMapper modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, UserRepository userRepository, UserMapper userMapper, EntitiesUtils entitiesUtils) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, userRepository, userMapper, "comment", List.of("id", "userId", "postId", "title"), entitiesUtils);
    }

    @Override
    public Mono<ExerciseResponse> deleteModel(Long id) {
        return validateExerciseNotUsed(id)
                .then(super.deleteModel(id));
    }

    @Override
    public Mono<ExerciseResponse> updateModel(Long id, ExerciseBody body) {
        return validateExerciseNotUsed(id)
                .then(super.updateModel(id, body));
    }


    private Mono<Void> validateExerciseNotUsed(Long id) {
        return modelRepository.countTrainingsByExerciseId(id)
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new SubEntityUsed("exercise", id));
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Flux<ResponseWithUserDto<ExerciseResponse>> getExercisesWithUserByIds(List<Long> ids) {
        return modelRepository.findAllById(ids)
                .flatMap(model -> userUtils.getUser(model.getUserId())
                        .map(user -> ResponseWithUserDto.<ExerciseResponse>builder()
                                .model(modelMapper.fromModelToResponse(model))
                                .user(userMapper.fromUserCustomToUserDto(user))
                                .build()));
    }

    @Override
    public Mono<Void> validIds(List<Long> ids) {
        return entitiesUtils.validIds(ids, modelRepository, modelName);
    }

    @Override
    public Mono<ExerciseTrainingCount> getTrainingCount(Long id) {
        return modelRepository.countTrainingsByExerciseId(id)
                .map(ExerciseTrainingCount::new);
    }

    @Override
    public Mono<ExerciseResponseWithTrainingCount> getExerciseWithTrainingCount(Long id) {
        return getModelById(id).then(modelRepository.findByIdWithTrainingCount(id))
                .switchIfEmpty(Mono.error(new NotFoundEntity("exercise", id)))
                .map(modelMapper::fromModelToResponseWithTrainingCount)
                .flatMap(model -> userUtils.getUser(model.getUserId())
                        .map(user -> {
                            model.setUser(userMapper.fromUserCustomToUserDto(user));
                            return model;
                        }));
    }

    @Override
    public Flux<ExerciseResponse> getApprovedModelsTrainer(Long trainerId) {
        return userUtils.getUser(trainerId)
                .thenMany(modelRepository.findAllByUserIdAndApprovedTrue(trainerId)
                        .map(modelMapper::fromModelToResponse));
    }


}
