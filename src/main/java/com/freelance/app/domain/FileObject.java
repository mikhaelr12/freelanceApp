package com.freelance.app.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A FileObject.
 */
@Table("file_object")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FileObject extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 80)
    @Column("bucket")
    private String bucket;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("object_key")
    private String objectKey;

    @Size(max = 120)
    @Column("content_type")
    private String contentType;

    @Column("file_size")
    private Long fileSize;

    @Size(max = 64)
    @Column("checksum")
    private String checksum;

    @Column("duration_seconds")
    private Integer durationSeconds;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FileObject id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBucket() {
        return this.bucket;
    }

    public FileObject bucket(String bucket) {
        this.setBucket(bucket);
        return this;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getObjectKey() {
        return this.objectKey;
    }

    public FileObject objectKey(String objectKey) {
        this.setObjectKey(objectKey);
        return this;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getContentType() {
        return this.contentType;
    }

    public FileObject contentType(String contentType) {
        this.setContentType(contentType);
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public FileObject fileSize(Long fileSize) {
        this.setFileSize(fileSize);
        return this;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getChecksum() {
        return this.checksum;
    }

    public FileObject checksum(String checksum) {
        this.setChecksum(checksum);
        return this;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Integer getDurationSeconds() {
        return this.durationSeconds;
    }

    public FileObject durationSeconds(Integer durationSeconds) {
        this.setDurationSeconds(durationSeconds);
        return this;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public FileObject createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public FileObject lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public FileObject createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public FileObject lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileObject)) {
            return false;
        }
        return getId() != null && getId().equals(((FileObject) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FileObject{" +
            "id=" + getId() +
            ", bucket='" + getBucket() + "'" +
            ", objectKey='" + getObjectKey() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", fileSize=" + getFileSize() +
            ", checksum='" + getChecksum() + "'" +
            ", durationSeconds=" + getDurationSeconds() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            "}";
    }
}
