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
 * A Country.
 */
@Table("country")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Country extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 128)
    @Column("name")
    private String name;

    @Size(max = 2)
    @Column("iso_2")
    private String iso2;

    @Size(max = 3)
    @Column("iso_3")
    private String iso3;

    @NotNull(message = "must not be null")
    @Size(max = 20)
    @Column("region")
    private String region;

    @NotNull(message = "must not be null")
    @Column("active")
    private Boolean active;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Country id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Country name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIso2() {
        return this.iso2;
    }

    public Country iso2(String iso2) {
        this.setIso2(iso2);
        return this;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getIso3() {
        return this.iso3;
    }

    public Country iso3(String iso3) {
        this.setIso3(iso3);
        return this;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public String getRegion() {
        return this.region;
    }

    public Country region(String region) {
        this.setRegion(region);
        return this;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Country createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public Country lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public Country createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public Country lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Country active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }
        return getId() != null && getId().equals(((Country) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Country{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", iso2='" + getIso2() + "'" +
            ", iso3='" + getIso3() + "'" +
            ", region='" + getRegion() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
