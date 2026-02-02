package com.freelance.app;

import com.freelance.app.config.AsyncSyncConfiguration;
import com.freelance.app.config.EmbeddedSQL;
import com.freelance.app.config.JacksonConfiguration;
import com.freelance.app.config.WebTestClientLoggerConfiguration;
import com.freelance.app.config.embedded.minio.EmbeddedMinIO;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = { FreelanceApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EmbeddedSQL
@EmbeddedMinIO
@Import(WebTestClientLoggerConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public @interface IntegrationTest {
    // 5s is Spring's default https://github.com/spring-projects/spring-framework/blob/main/spring-test/src/main/java/org/springframework/test/web/reactive/server/DefaultWebTestClient.java#L106
    String DEFAULT_TIMEOUT = "PT5S";
}
