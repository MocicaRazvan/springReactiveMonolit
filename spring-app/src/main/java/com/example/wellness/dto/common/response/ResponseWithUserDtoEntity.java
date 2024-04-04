package com.example.wellness.dto.common.response;

import com.example.wellness.dto.common.UserDto;
import com.example.wellness.hateos.CustomEntityModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.EntityModel;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWithUserDtoEntity<T> {
    private CustomEntityModel<T> model;
    private CustomEntityModel<UserDto> user;
}
