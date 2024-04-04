package com.example.wellness.dto.exercise;

import com.example.wellness.models.Exercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseWithTrainingCount extends Exercise {
    private Long trainingCount;
}
