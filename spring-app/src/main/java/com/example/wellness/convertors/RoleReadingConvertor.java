package com.example.wellness.convertors;


import com.example.wellness.enums.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class RoleReadingConvertor implements Converter<String, Role> {
    @Override
    public Role convert(String source) {
        return source == null ? null : Role.valueOf(source);
    }
}
