package com.freelance.app.config.embedded.minio;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;

/**
 * Factory to customize Spring context for MinIO Testcontainers in integration tests.
 * Detects @EmbeddedMinIO annotation and starts MinIO container, injecting properties.
 */
public class MinIOTestContainersSpringContextCustomizerFactory implements ContextCustomizerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(MinIOTestContainersSpringContextCustomizerFactory.class);

    // Static to reuse container across tests
    private static MinIOTestContainer minioTestContainer;

    @Override
    public ContextCustomizer createContextCustomizer(
        @NotNull Class<?> testClass,
        @NotNull List<ContextConfigurationAttributes> configAttributes
    ) {
        return new ContextCustomizer() {
            @Override
            public void customizeContext(
                @NotNull ConfigurableApplicationContext context,
                @NotNull MergedContextConfiguration mergedConfig
            ) {
                TestPropertyValues testValues = TestPropertyValues.empty();
                EmbeddedMinIO annotation = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedMinIO.class);
                if (annotation != null) {
                    LOG.debug("Detected the @EmbeddedMinIO annotation on class {}", testClass.getName());
                    LOG.info("Warming up the minio container");
                    // Initialize container only once
                    if (minioTestContainer == null) {
                        try {
                            minioTestContainer = context.getBeanFactory().createBean(MinIOTestContainer.class);
                            // Register as singleton for lifecycle management
                            context.getBeanFactory().registerSingleton(MinIOTestContainer.class.getName(), minioTestContainer);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to create MinIOTestContainer", e);
                        }
                    }
                    var container = minioTestContainer.getTestContainer();
                    // Inject MinIO properties (adjust to match your app's configuration)
                    testValues = testValues.and("application.minio.url=" + container.getS3URL());
                    testValues = testValues.and("application.minio.access-key=" + container.getUserName());
                    testValues = testValues.and("application.minio.secret-key=" + container.getPassword());
                    testValues = testValues.and("application.minio.bucket-name=test");
                }
                testValues.applyTo(context);
            }

            @Override
            public int hashCode() {
                return MinIOTestContainer.class.getName().hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return this.hashCode() == obj.hashCode();
            }
        };
    }
}
