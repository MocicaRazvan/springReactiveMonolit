package com.example.wellness.hateos.linking.impl.links;

import com.example.wellness.controllers.ExerciseController;
import com.example.wellness.dto.exercise.ExerciseBody;
import com.example.wellness.dto.exercise.ExerciseResponse;
import com.example.wellness.hateos.linking.generics.ApproveReactiveLinkBuilder;
import com.example.wellness.mappers.ExerciseMapper;
import com.example.wellness.models.Exercise;
import com.example.wellness.repositories.ExerciseRepository;
import com.example.wellness.services.ExerciseService;

public class ExerciseReactiveLinkBuilder extends ApproveReactiveLinkBuilder<Exercise, ExerciseBody, ExerciseResponse, ExerciseRepository, ExerciseMapper, ExerciseService, ExerciseController> {


}
