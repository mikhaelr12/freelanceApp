package com.freelance.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Skill.
 */
@Table("skill")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Skill extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 128)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("active")
    private Boolean active;

    @org.springframework.data.annotation.Transient
    private Category category;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "profilePicture", "skills" }, allowSetters = true)
    private Set<Profile> profiles = new HashSet<>();

    @Column("category_id")
    private Long categoryId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Skill id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Skill name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Skill active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.categoryId = category != null ? category.getId() : null;
    }

    public Skill category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Set<Profile> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        if (this.profiles != null) {
            this.profiles.forEach(i -> i.removeSkill(this));
        }
        if (profiles != null) {
            profiles.forEach(i -> i.addSkill(this));
        }
        this.profiles = profiles;
    }

    public Skill profiles(Set<Profile> profiles) {
        this.setProfiles(profiles);
        return this;
    }

    public Skill addProfile(Profile profile) {
        this.profiles.add(profile);
        profile.getSkills().add(this);
        return this;
    }

    public Skill removeProfile(Profile profile) {
        this.profiles.remove(profile);
        profile.getSkills().remove(this);
        return this;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long category) {
        this.categoryId = category;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Skill)) {
            return false;
        }
        return getId() != null && getId().equals(((Skill) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Skill{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
