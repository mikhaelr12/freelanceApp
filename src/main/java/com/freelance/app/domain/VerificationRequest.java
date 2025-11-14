package com.freelance.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freelance.app.domain.enumeration.VerificationRequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A VerificationRequest.
 */
@Table("verification_request")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VerificationRequest extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "profilePicture", "skills" }, allowSetters = true)
    private Profile profile;

    @org.springframework.data.annotation.Transient
    private FileObject fileObject;

    @Column("profile_id")
    private Long profileId;

    @Column("file_object_id")
    private Long fileObjectId;

    @Column("status")
    private VerificationRequestStatus status;

    @Column("message")
    private String message;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VerificationRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
        this.profileId = profile != null ? profile.getId() : null;
    }

    public VerificationRequest profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    public FileObject getFileObject() {
        return this.fileObject;
    }

    public void setFileObject(FileObject fileObject) {
        this.fileObject = fileObject;
        this.fileObjectId = fileObject != null ? fileObject.getId() : null;
    }

    public VerificationRequest fileObject(FileObject fileObject) {
        this.setFileObject(fileObject);
        return this;
    }

    public Long getProfileId() {
        return this.profileId;
    }

    public void setProfileId(Long profile) {
        this.profileId = profile;
    }

    public Long getFileObjectId() {
        return this.fileObjectId;
    }

    public void setFileObjectId(Long fileObject) {
        this.fileObjectId = fileObject;
    }

    public VerificationRequestStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationRequestStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VerificationRequest createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public VerificationRequest createdDate(Instant createdOn) {
        this.setCreatedDate(createdOn);
        return this;
    }

    public VerificationRequest lastModifiedDate(Instant lastModifiedOn) {
        this.setLastModifiedDate(lastModifiedOn);
        return this;
    }

    public VerificationRequest lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public VerificationRequest status(VerificationRequestStatus status) {
        this.setStatus(status);
        return this;
    }

    public VerificationRequest message(String message) {
        this.setMessage(message);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VerificationRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((VerificationRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VerificationRequest{" +
            "id=" + getId() +
            "}";
    }
}
