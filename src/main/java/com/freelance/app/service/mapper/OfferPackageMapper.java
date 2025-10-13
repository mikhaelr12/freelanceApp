package com.freelance.app.service.mapper;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.Offer;
import com.freelance.app.domain.OfferPackage;
import com.freelance.app.service.dto.FavoriteOfferDTO;
import com.freelance.app.service.dto.OfferDTO;
import com.freelance.app.service.dto.OfferPackageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OfferPackage} and its DTO {@link OfferPackageDTO}.
 */
@Mapper(componentModel = "spring")
public interface OfferPackageMapper extends EntityMapper<OfferPackageDTO, OfferPackage> {
    @Mapping(target = "offer", source = "offer", qualifiedByName = "offerName")
    OfferPackageDTO toDto(OfferPackage s);

    @Named("offerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OfferDTO toDtoOfferName(Offer offer);

    @BeanMapping(ignoreByDefault = true)
    OfferPackage toEntity(OfferPackageDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    void partialUpdate(@MappingTarget OfferPackage entity, OfferPackageDTO dto);
}
