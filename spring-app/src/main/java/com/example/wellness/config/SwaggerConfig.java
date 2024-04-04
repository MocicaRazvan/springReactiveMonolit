package com.example.wellness.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {
    @Value("${wellness.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI openAPI() {
        Server devServer = new Server().url(devUrl).description("Development ENV");


        Contact contact = new Contact().name("Mocica Razvan").email("razvanmocica@gmail.com");

        License mitLicense = new License().name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info().contact(contact).license(mitLicense)
                .description("Wellness API Documentation")
                .title("Wellness Specification")
                .version("1.0")
                .termsOfService("Terms of service");

        @SuppressWarnings("unchecked")
        Schema<Map<String, Object>> withMsg = new Schema<Map<String, Object>>()
                .name("Standard response with message")
                .title("WithMessage")
                .description("Contains information about the reasons of the bad response");

        createRespWithMsgProperties(withMsg);

        @SuppressWarnings("unchecked")
        Schema<Map<String, Object>> authMsg = new Schema<Map<String, Object>>()
                .name("Authentication error response")
                .title("AuthMessage")
                .description("Contains information about the authentication bad response.");


        createRespWithMsgProperties(authMsg, "User");

        @SuppressWarnings("unchecked")
        Schema<Map<String, Object>> validation = new Schema<Map<String, Object>>()
                .name("Validation response")
                .title("ValidationResponse")
                .description("Contains information about the reasons of the validation failure");
        createRespWithMsgProperties(validation);
        validation.addProperty("reasons", new StringSchema().description("The field errors for validation"));


        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer AUTH", Arrays.asList("read", "write"));

        Components components = new Components()
                .addSecuritySchemes("Bearer AUTH", securityScheme())
                .addSchemas(withMsg.getTitle(), withMsg)
                .addSchemas(authMsg.getTitle(), authMsg);

        return new OpenAPI().info(info).servers(List.of(devServer))
                .addSecurityItem(securityRequirement)
                .components(components)
                ;
    }


    private SecurityScheme securityScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization");

    }

    private void createRespWithMsgProperties(Schema<Map<String, Object>> schema) {
        createRespWithMsgProperties(schema, "Post");
    }

    private void createRespWithMsgProperties(Schema<Map<String, Object>> schema, String entity) {
        schema.addProperty("message", new StringSchema().example(entity + " with id: 1 was not found")
                .description("The actual error that was thrown be the application"));
        schema.addProperty("timestamp", new StringSchema()
                .description("The timestamp of the request"));
        schema.addProperty("error", new StringSchema()
                .description("The reason of the status"));
        schema.addProperty("status", new IntegerSchema().example(400)
                .description("The error status"));
        schema.addProperty("path", new StringSchema().example("http://localhost:8080/posts")
                .description("The path of the request"));
    }
}
