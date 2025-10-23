package com.freelance.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Freelance App.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    // jhipster-needle-application-properties-property

    // jhipster-needle-application-properties-property-getter

    // jhipster-needle-application-properties-property-class

    private MinioConfiguration minio;

    public MinioConfiguration getMinio() {
        return minio;
    }

    public void setMinio(MinioConfiguration minio) {
        this.minio = minio;
    }

    public static class MinioConfiguration {

        private String url;
        private String accessKey;
        private String secretKey;
        private String bucketName;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }
}
