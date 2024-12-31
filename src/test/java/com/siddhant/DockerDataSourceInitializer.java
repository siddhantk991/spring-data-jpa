package com.siddhant;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// JUnit-Jupiter extension which automatically starts and stops the containers that are used in the tests.
// this class must be extended by any other test classes to use test-container
@Testcontainers
// A class-level annotation that is used to declare which active bean definition profiles should be used when loading an ApplicationContext for test classes
@ActiveProfiles("test")
public abstract class DockerDataSourceInitializer  implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    // Annotation used in conjunction with the Testcontainers annotation to mark containers that should be managed by the Testcontainers extension.
    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:12");

    /*
       `initialize` function allows us to set properties dynamically. Since the DataSource is initialized dynamically,
        we need to set url, username, and password that is provided/set by the testcontainers.
     */
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.test.database.replace=none", // Tells Spring Boot not to start in-memory db for tests.
                "spring.datasource.url=" + database.getJdbcUrl(),
                "spring.datasource.username=" + database.getUsername(),
                "spring.datasource.password=" + database.getPassword()
        );
    }
}

