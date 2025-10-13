package com.freelance.app.service.mapper;

import com.freelance.app.domain.Conversation;
import com.freelance.app.domain.Offer;
import com.freelance.app.domain.OfferReview;
import com.freelance.app.domain.Profile;
import com.freelance.app.service.dto.ConversationDTO;
import com.freelance.app.service.dto.OfferDTO;
import com.freelance.app.service.dto.OfferReviewDTO;
import com.freelance.app.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OfferReview} and its DTO {@link OfferReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface OfferReviewMapper extends EntityMapper<OfferReviewDTO, OfferReview> {
    @Mapping(target = "offer", source = "offer", qualifiedByName = "offerName")
    @Mapping(target = "reviewer", source = "reviewer", qualifiedByName = "profileId")
    OfferReviewDTO toDto(OfferReview s);

    @Named("offerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OfferDTO toDtoOfferName(Offer offer);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);

    @BeanMapping(ignoreByDefault = true)
    OfferReview toEntity(OfferReviewDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    void partialUpdate(@MappingTarget OfferReview entity, OfferReviewDTO dto);
}
