package com.example.wellness.hateos.linking.impl.responses;

import com.example.wellness.controllers.ExerciseController;
import com.example.wellness.dto.exercise.ExerciseResponse;
import com.example.wellness.hateos.linking.ReactiveResponseBuilder;
import com.example.wellness.hateos.linking.impl.links.ExerciseReactiveLinkBuilder;
import com.example.wellness.hateos.user.UserDtoAssembler;
import org.springframework.stereotype.Component;


@Component
public class ExerciseReactiveResponseBuilder extends ReactiveResponseBuilder<ExerciseResponse, ExerciseController> {
    public ExerciseReactiveResponseBuilder(UserDtoAssembler userDtoAssembler) {
        super(userDtoAssembler, new ExerciseReactiveLinkBuilder());
    }
}
