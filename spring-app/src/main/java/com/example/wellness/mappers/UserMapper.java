package com.example.wellness.mappers;

import com.example.wellness.dto.auth.AuthResponse;
import com.example.wellness.dto.auth.RegisterRequest;
import com.example.wellness.dto.common.UserBody;
import com.example.wellness.dto.common.UserDto;
import com.example.wellness.models.user.UserCustom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(registerRequest.getPassword()))")
    @Mapping(target = "role", expression = "java(com.example.wellness.enums.Role.ROLE_USER)")
    public abstract UserCustom fromRegisterRequestToUserCustom(RegisterRequest registerRequest);

    public abstract AuthResponse fromUserCustomToAuthResponse(UserCustom userCustom);

    public abstract UserDto fromUserCustomToUserDto(UserCustom userCustom);

    public abstract UserBody fromUserDtoToUserBody(UserDto userDto);
}
