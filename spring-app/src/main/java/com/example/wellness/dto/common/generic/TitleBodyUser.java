package com.example.wellness.dto.common.generic;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Schema(description = "The basic dto for an entity that contains title, body and a user")
public abstract class TitleBodyUser extends WithUser {
    @NotBlank(message = "The body should not be blank.")
    @Schema(description = "The entity's body")
    private String body;

    @NotBlank(message = "The title should not be blank.")
    @Schema(description = "The entity's title")
    private String title;

    @Schema(description = "The user ids that liked the entity")
    private List<Long> userDislikes = new ArrayList<>();

    @Schema(description = "The user ids that disliked the entity")
    private List<Long> userLikes = new ArrayList<>();

    private List<String> images;
}
