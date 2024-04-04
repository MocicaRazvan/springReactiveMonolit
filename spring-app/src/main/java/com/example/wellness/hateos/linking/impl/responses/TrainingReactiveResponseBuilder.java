package com.example.wellness.hateos.linking.impl.responses;


import com.example.wellness.controllers.TrainingController;
import com.example.wellness.dto.training.TrainingResponse;
import com.example.wellness.hateos.linking.ReactiveResponseBuilder;
import com.example.wellness.hateos.linking.impl.links.TrainingReactiveLinkBuilder;
import com.example.wellness.hateos.user.UserDtoAssembler;
import org.springframework.stereotype.Component;

@Component
public class TrainingReactiveResponseBuilder extends ReactiveResponseBuilder<TrainingResponse, TrainingController> {
    public TrainingReactiveResponseBuilder(UserDtoAssembler userDtoAssembler) {
        super(userDtoAssembler, new TrainingReactiveLinkBuilder());
    }
}
