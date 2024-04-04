package com.example.wellness.convertors;

import com.example.wellness.enums.Role;
import org.springframework.data.convert.WritingConverter;
import org.springframework.core.convert.converter.Converter;

@WritingConverter
public class RoleWritingConvertor implements Converter<Role, String> {
    @Override
    public String convert(Role role) {
        return role == null ? null : role.name();
    }
}
