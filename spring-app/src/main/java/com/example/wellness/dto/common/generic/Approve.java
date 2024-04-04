package com.example.wellness.dto.common.generic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Schema(description = "The default schema for an approved type entity")
public abstract class Approve extends TitleBodyUser {
    @Schema(description = "The state of the approved status for the entity")
    private boolean approved = false;
}
