package com.example.wellness.dto.common.response;

import com.example.wellness.dto.common.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Data
@SuperBuilder
@Schema(description = "The comment response dto")
@AllArgsConstructor
public class ResponseWithUserDto<T> {
    private T model;
    private UserDto user;
}
