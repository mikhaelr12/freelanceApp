package com.freelance.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freelance.app.domain.enumeration.ProfileType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Profile.
 */
@Table("profile")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Profile extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 20)
    @Column("first_name")
    private String firstName;

    @NotNull(message = "must not be null")
    @Size(max = 20)
    @Column("last_name")
    private String lastName;

    @Size(max = 2048)
    @Column("description")
    private String description;

    @Column("rating")
    private Double rating;

    @Column("profile_type")
    private ProfileType profileType;

    @org.springframework.data.annotation.Transient
    private User user;

    @org.springframework.data.annotation.Transient
    private FileObject profilePicture;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "category", "profiles" }, allowSetters = true)
    private Set<Skill> skills = new HashSet<>();

    @Column("user_id")
    private Long userId;

    @Column("profile_picture_id")
    private Long profilePictureId;

    @Column("verified")
    private Boolean verified;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Profile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Profile firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Profile lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return this.description;
    }

    public Profile description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Profile createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public Profile lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public Profile createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public Profile lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public ProfileType getProfileType() {
        return this.profileType;
    }

    public Profile profileType(ProfileType profileType) {
        this.setProfileType(profileType);
        return this;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Profile user(User user) {
        this.setUser(user);
        return this;
    }

    public FileObject getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(FileObject fileObject) {
        this.profilePicture = fileObject;
        this.profilePictureId = fileObject != null ? fileObject.getId() : null;
    }

    public Profile profilePicture(FileObject fileObject) {
        this.setProfilePicture(fileObject);
        return this;
    }

    public Set<Skill> getSkills() {
        return this.skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public Profile skills(Set<Skill> skills) {
        this.setSkills(skills);
        return this;
    }

    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getProfilePictureId() {
        return this.profilePictureId;
    }

    public void setProfilePictureId(Long fileObject) {
        this.profilePictureId = fileObject;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Profile verified(Boolean verified) {
        this.setVerified(verified);
        return this;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Profile)) {
            return false;
        }
        return getId() != null && getId().equals(((Profile) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Profile{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", description='" + getDescription() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", profileType='" + getProfileType() + "'" +
            "}";
    }
}
