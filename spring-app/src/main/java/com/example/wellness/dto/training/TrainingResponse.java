package com.example.wellness.dto.training;

import com.example.wellness.dto.common.generic.Approve;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Schema(description = "The training response dto")
public class TrainingResponse extends Approve {
    @Schema(description = "The price of the training")
    private double price;

    @Schema(description = "The exercise's ids for the training.")
    private List<Long> exercises;
}
