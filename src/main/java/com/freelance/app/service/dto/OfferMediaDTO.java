package com.freelance.app.service.dto;

import com.freelance.app.domain.enumeration.MediaKind;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.freelance.app.domain.OfferMedia} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OfferMediaDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private MediaKind mediaKind;

    @NotNull(message = "must not be null")
    private Boolean isPrimary;

    @Size(max = 140)
    private String caption;

    @NotNull(message = "must not be null")
    private Instant createdDate;

    private Instant lastModifiedDate;

    @Size(max = 50)
    private String createdBy;

    @Size(max = 50)
    private String lastModifiedBy;

    private OfferDTO offer;

    private FileObjectDTO file;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MediaKind getMediaKind() {
        return mediaKind;
    }

    public void setMediaKind(MediaKind mediaKind) {
        this.mediaKind = mediaKind;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public OfferDTO getOffer() {
        return offer;
    }

    public void setOffer(OfferDTO offer) {
        this.offer = offer;
    }

    public FileObjectDTO getFile() {
        return file;
    }

    public void setFile(FileObjectDTO file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OfferMediaDTO)) {
            return false;
        }

        OfferMediaDTO offerMediaDTO = (OfferMediaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, offerMediaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OfferMediaDTO{" +
            "id=" + getId() +
            ", mediaKind='" + getMediaKind() + "'" +
            ", isPrimary='" + getIsPrimary() + "'" +
            ", caption='" + getCaption() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", offer=" + getOffer() +
            ", file=" + getFile() +
            "}";
    }
}
