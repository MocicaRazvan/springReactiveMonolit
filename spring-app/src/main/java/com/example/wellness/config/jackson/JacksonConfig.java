package com.example.wellness.config.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@Slf4j
public class JacksonConfig {
    @Bean
    public ObjectMapper customObjectMapper(final Jackson2ObjectMapperBuilder builder) {
        return builder
                .modules(new SimpleModule().addSerializer(WebFluxLinkBuilder.WebFluxLink.class, new WebFluxLinkSerializer())
//                        , new SimpleModule().addSerializer(EntityModel.class, new EntityModelSerializer())
                        , new Jackson2HalModule(), new JavaTimeModule()
                )
                .build();
    }


}
