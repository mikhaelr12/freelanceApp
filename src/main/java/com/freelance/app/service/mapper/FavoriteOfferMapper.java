package com.freelance.app.service.mapper;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.Offer;
import com.freelance.app.domain.Profile;
import com.freelance.app.service.dto.FavoriteOfferDTO;
import com.freelance.app.service.dto.OfferDTO;
import com.freelance.app.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FavoriteOffer} and its DTO {@link FavoriteOfferDTO}.
 */
@Mapper(componentModel = "spring")
public interface FavoriteOfferMapper extends EntityMapper<FavoriteOfferDTO, FavoriteOffer> {
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileId")
    @Mapping(target = "offer", source = "offer", qualifiedByName = "offerId")
    FavoriteOfferDTO toDto(FavoriteOffer s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);

    @Named("offerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OfferDTO toDtoOfferId(Offer offer);

    @BeanMapping(ignoreByDefault = true)
    FavoriteOffer toEntity(FavoriteOfferDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    void partialUpdate(@MappingTarget FavoriteOffer entity, FavoriteOfferDTO dto);
}
