package com.freelance.app.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.freelance.app.domain.Skill} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SkillDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 128)
    private String name;

    @NotNull(message = "must not be null")
    private Instant createdDate;

    private Instant lastModifiedDate;

    @Size(max = 50)
    private String createdBy;

    @Size(max = 50)
    private String lastModifiedBy;

    @NotNull(message = "must not be null")
    private Boolean active;

    private CategoryDTO category;

    private Set<ProfileDTO> profiles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public Set<ProfileDTO> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<ProfileDTO> profiles) {
        this.profiles = profiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SkillDTO)) {
            return false;
        }

        SkillDTO skillDTO = (SkillDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, skillDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SkillDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", active='" + getActive() + "'" +
            ", category=" + getCategory() +
            ", profiles=" + getProfiles() +
            "}";
    }
}
