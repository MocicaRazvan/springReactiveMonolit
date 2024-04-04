package com.example.wellness.hateos.linking.impl.links;

import com.example.wellness.controllers.TrainingController;
import com.example.wellness.dto.training.TrainingBody;
import com.example.wellness.dto.training.TrainingResponse;
import com.example.wellness.hateos.linking.generics.ApproveReactiveLinkBuilder;
import com.example.wellness.mappers.TrainingMapper;
import com.example.wellness.models.Training;
import com.example.wellness.repositories.TrainingRepository;
import com.example.wellness.services.TrainingService;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;

public class TrainingReactiveLinkBuilder extends ApproveReactiveLinkBuilder<Training, TrainingBody, TrainingResponse, TrainingRepository, TrainingMapper, TrainingService, TrainingController> {

    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(TrainingResponse trainingResponse, Class<TrainingController> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = super.createModelLinks(trainingResponse, c);
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getTrainingWithExercises(trainingResponse.getId())).withRel("getWithExercises"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getTrainingsWithExercises(List.of(1L, 2L))).withRel("getApprovedWithExercises"));
        return links;
    }

}
