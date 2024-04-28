package com.example.wellness.services.impl;

import com.example.wellness.dto.common.response.ResponseWithChildList;
import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.dto.exercise.ExerciseResponse;
import com.example.wellness.dto.training.TrainingBody;
import com.example.wellness.dto.training.TrainingResponse;
import com.example.wellness.dto.training.TrainingResponseWithOrderCount;
import com.example.wellness.exceptions.action.SubEntityUsed;
import com.example.wellness.exceptions.notFound.NotFoundEntity;
import com.example.wellness.mappers.TrainingMapper;
import com.example.wellness.mappers.UserMapper;
import com.example.wellness.models.Training;
import com.example.wellness.repositories.TrainingRepository;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.services.ExerciseService;
import com.example.wellness.services.TrainingService;
import com.example.wellness.services.impl.generics.ApprovedServiceImpl;
import com.example.wellness.utils.EntitiesUtils;
import com.example.wellness.utils.PageableUtilsCustom;
import com.example.wellness.utils.UserUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
public class TrainingServiceImpl extends ApprovedServiceImpl<Training, TrainingBody, TrainingResponse, TrainingRepository, TrainingMapper>
        implements TrainingService {

    private final ExerciseService exerciseService;

    public TrainingServiceImpl(TrainingRepository modelRepository, TrainingMapper modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, UserRepository userRepository, UserMapper userMapper, EntitiesUtils entitiesUtils, ExerciseService exerciseService) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, userRepository, userMapper, "training", List.of("id", "userId", "title", "createdAt", "price"), entitiesUtils);
        this.exerciseService = exerciseService;
    }

    @Override
    public Mono<ResponseWithChildList<TrainingResponse, ResponseWithUserDto<ExerciseResponse>>> getTrainingWithExercises(Long id, boolean approved) {
        return modelRepository.findByApprovedAndId(approved, id)
                .switchIfEmpty(Mono.error(new NotFoundEntity("training", id)))
                .flatMap(training -> exerciseService.getExercisesWithUserByIds(training.getExercises())
                        .collectList()
                        .map(comments -> new ResponseWithChildList<>(modelMapper.fromModelToResponse(training), comments))
                );
    }

    @Override
    public Flux<ResponseWithChildList<TrainingResponse, ResponseWithUserDto<ExerciseResponse>>> getTrainingsWithExercises(List<Long> ids, boolean approved) {
        return modelRepository.findAllByApprovedAndIdIn(approved, ids)
                .flatMap(training -> exerciseService.getExercisesWithUserByIds(training.getExercises())
                        .collectList()
                        .map(comments -> new ResponseWithChildList<>(modelMapper.fromModelToResponse(training), comments))
                );
    }

    @Override
    public Mono<Double> getTotalPriceById(List<Long> ids) {
        return modelRepository.sumPriceByIds(ids);
    }

    @Override
    public Mono<TrainingResponseWithOrderCount> getTrainingWithOrderCount(Long id) {
        return getModelById(id).then(modelRepository.findByIdWithOrderCount(id))
                .switchIfEmpty(Mono.error(new NotFoundEntity("training", id)))
                .map(modelMapper::fromModelToResponseWithOrderCount)
                .flatMap(model -> userUtils.getUser(model.getUserId())
                        .map(user -> {
                            model.setUser(userMapper.fromUserCustomToUserDto(user));
                            return model;
                        }));

    }

    @Override
    public Mono<TrainingResponse> createModel(TrainingBody body) {
        return exerciseService.validIds(body.getExercises())
                .then(modelMapper.updateModelFromBody(body, new Training()))
                .flatMap(training -> {
                    training.setApproved(false);
                    return userUtils.getPrincipal()
                            .flatMap(user -> {
                                training.setUserId(user.getId());
                                return modelRepository.save(training).map(modelMapper::fromModelToResponse);
                            });
                });
    }

    @Override
    public Mono<TrainingResponse> deleteModel(Long id) {
        return modelRepository.countOrdersByTrainingId(id)
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new SubEntityUsed("training", id));
                    }
                    return super.deleteModel(id);
                });
    }

    @Override
    public Mono<Void> validIds(List<Long> ids) {
        return entitiesUtils.validIds(ids, modelRepository, modelName);
    }
}
