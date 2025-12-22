package com.freelance.app.service.dto;

import java.util.Set;

public class OfferDTO {

    private String name;
    private String description;
    private Long offerTypeId;
    private Set<Long> tagIds;

    public OfferDTO() {}

    public OfferDTO(String name, String description, Long offerTypeId, Set<Long> tagIds) {
        this.name = name;
        this.description = description;
        this.offerTypeId = offerTypeId;
        this.tagIds = tagIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOfferTypeId() {
        return offerTypeId;
    }

    public void setOfferTypeId(Long offerTypeId) {
        this.offerTypeId = offerTypeId;
    }

    public Set<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds;
    }
}
