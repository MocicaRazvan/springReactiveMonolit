package com.example.wellness.dto.training;


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
public class TrainingResponseWithOrderCount extends TrainingResponse {
    private Long orderCount;
    private UserDto user;
}
