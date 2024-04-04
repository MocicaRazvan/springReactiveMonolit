package com.example.wellness.hateos.user;


import com.example.wellness.dto.common.UserDto;
import org.springframework.stereotype.Component;

@Component
public class PageableUserAssembler extends PageableResponseAssembler<UserDto, UserDtoAssembler> {
    public PageableUserAssembler(UserDtoAssembler itemAssembler) {
        super(itemAssembler);
    }
}
