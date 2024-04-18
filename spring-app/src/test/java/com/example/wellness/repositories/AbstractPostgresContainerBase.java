package com.example.wellness.repositories;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(parallel = true)
public abstract class AbstractPostgresContainerBase {

    @Container
    static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testDb")
            .withUsername("root")
            .withPassword("123456")
            .withInitScript("schema-tc.sql");

    @BeforeAll
    static void startContainer() {
        postgresqlContainer.start();
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> String.format("r2dbc:postgresql://%s:%d/%s",
                postgresqlContainer.getHost(),
                postgresqlContainer.getFirstMappedPort(),
                postgresqlContainer.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgresqlContainer::getUsername);
        registry.add("spring.r2dbc.password", postgresqlContainer::getPassword);
    }

    @AfterAll
    static void stopContainer() {
        postgresqlContainer.stop();
    }
}
