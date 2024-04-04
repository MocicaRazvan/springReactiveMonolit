package com.example.wellness.dto.common.generic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public abstract class IdDto {

    @Schema(description = "The entity's id")
    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
