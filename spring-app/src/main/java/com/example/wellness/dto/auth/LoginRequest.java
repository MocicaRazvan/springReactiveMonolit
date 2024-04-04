package com.example.wellness.dto.auth;

import com.example.wellness.utils.Transformable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Schema(description = "The object provided by the user to login")
public class LoginRequest implements Transformable<LoginRequest> {
    @NotBlank(message = "Email should be not empty!")
    @Email(message = "Email should be valid!")
    @Schema(description = "The user email")
    private String email;

    @NotBlank(message = "Password should be not empty!")
    @Length(min = 4, message = "Password should be at least 4 characters!")
    @Schema(description = "The user actual password")
    private String password;
}
