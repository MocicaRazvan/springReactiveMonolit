package com.example.wellness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
//@EnableWebFlux
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
//@EnableR2dbcAuditing
public class WellnessApplication {

    public static void main(String[] args) {
        SpringApplication.run(WellnessApplication.class, args);
    }

}
