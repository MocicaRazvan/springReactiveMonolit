package com.example.wellness.dto.auth;

import com.example.wellness.enums.Role;
import com.example.wellness.utils.Transformable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "The object sent as the response when authentication is succesfull")
public class AuthResponse implements Transformable<AuthResponse> {

    private Long id;

    @Schema(description = "User's first name")
    private String firstName;

    @Schema(description = "User's last name")
    private String lastName;

    @Schema(description = "User's email")
    private String email;

    @Schema(description = "Generated JWT for the user")
    private String token;

    private Role role;

    private String image;
}
