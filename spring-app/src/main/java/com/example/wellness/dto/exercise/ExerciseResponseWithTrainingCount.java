package com.example.wellness.dto.exercise;

import com.example.wellness.dto.common.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExerciseResponseWithTrainingCount extends ExerciseResponse {
    private Long trainingCount;
    private UserDto user;
}
