package com.freelance.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freelance.app.domain.enumeration.MediaKind;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A OfferMedia.
 */
@Table("offer_media")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OfferMedia extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("media_kind")
    private MediaKind mediaKind;

    @NotNull(message = "must not be null")
    @Column("is_primary")
    private Boolean isPrimary;

    @Size(max = 140)
    @Column("caption")
    private String caption;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "owner", "offertype", "tags" }, allowSetters = true)
    private Offer offer;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "verificationRequest" }, allowSetters = true)
    private FileObject file;

    @Column("offer_id")
    private Long offerId;

    @Column("file_id")
    private Long fileId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OfferMedia id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MediaKind getMediaKind() {
        return this.mediaKind;
    }

    public OfferMedia mediaKind(MediaKind mediaKind) {
        this.setMediaKind(mediaKind);
        return this;
    }

    public void setMediaKind(MediaKind mediaKind) {
        this.mediaKind = mediaKind;
    }

    public Boolean getIsPrimary() {
        return this.isPrimary;
    }

    public OfferMedia isPrimary(Boolean isPrimary) {
        this.setIsPrimary(isPrimary);
        return this;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getCaption() {
        return this.caption;
    }

    public OfferMedia caption(String caption) {
        this.setCaption(caption);
        return this;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Offer getOffer() {
        return this.offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        this.offerId = offer != null ? offer.getId() : null;
    }

    public OfferMedia offer(Offer offer) {
        this.setOffer(offer);
        return this;
    }

    public FileObject getFile() {
        return this.file;
    }

    public void setFile(FileObject fileObject) {
        this.file = fileObject;
        this.fileId = fileObject != null ? fileObject.getId() : null;
    }

    public OfferMedia file(FileObject fileObject) {
        this.setFile(fileObject);
        return this;
    }

    public Long getOfferId() {
        return this.offerId;
    }

    public void setOfferId(Long offer) {
        this.offerId = offer;
    }

    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(Long fileObject) {
        this.fileId = fileObject;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OfferMedia)) {
            return false;
        }
        return getId() != null && getId().equals(((OfferMedia) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OfferMedia{" +
            "id=" + getId() +
            ", mediaKind='" + getMediaKind() + "'" +
            ", isPrimary='" + getIsPrimary() + "'" +
            ", caption='" + getCaption() + "'" +
            "}";
    }
}
