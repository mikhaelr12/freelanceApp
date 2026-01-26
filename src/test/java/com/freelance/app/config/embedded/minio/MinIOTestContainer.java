package com.freelance.app.config.embedded.minio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

public class MinIOTestContainer implements InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(MinIOTestContainer.class);

    private MinIOContainer minioContainer;

    @Override
    public void afterPropertiesSet() {
        if (minioContainer == null) {
            minioContainer = new MinIOContainer("minio/minio:latest")
                .withUserName("minioadmin")
                .withPassword("minioadmin")
                .withLogConsumer(new Slf4jLogConsumer(LOG))
                .withReuse(true);
        }
        if (!minioContainer.isRunning()) {
            minioContainer.start();
            LOG.info("Started Minio container at {}", minioContainer.getS3URL());
        }
    }

    @Override
    public void destroy() {
        if (minioContainer != null && minioContainer.isRunning()) {
            minioContainer.stop();
        }
    }

    public MinIOContainer getTestContainer() {
        return minioContainer;
    }
}
