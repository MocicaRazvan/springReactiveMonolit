package com.example.wellness.mappers;

import com.example.wellness.dto.training.TrainingBody;
import com.example.wellness.dto.training.TrainingResponse;
import com.example.wellness.dto.training.TrainingResponseWithOrderCount;
import com.example.wellness.dto.training.TrainingWithOrderCount;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.Training;
import com.example.wellness.utils.EntitiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class TrainingMapper extends DtoMapper<Training, TrainingBody, TrainingResponse> {

    @Autowired
    private EntitiesUtils entitiesUtils;


    @Override
    public Mono<Training> updateModelFromBody(TrainingBody body, Training training) {
        return entitiesUtils.verifyMappingExercises(body.getExercises())
                .then(Mono.fromCallable(
                        () -> {
                            training.setBody(body.getBody());
                            training.setTitle(body.getTitle());
                            training.setExercises(
                                    body.getExercises()
                                            .stream().distinct().toList()
                            );
                            training.setPrice(body.getPrice());
                            training.setApproved(false);
                            training.setUserDislikes(new ArrayList<>());
                            training.setUserLikes(new ArrayList<>());
                            training.setImages(body.getImages());
                            return training;
                        }
                ));
    }

    public abstract TrainingResponseWithOrderCount fromModelToResponseWithOrderCount(TrainingWithOrderCount trainingWithOrderCount);
}
