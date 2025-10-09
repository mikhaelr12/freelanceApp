package com.freelance.app.service.mapper;

import com.freelance.app.domain.Offer;
import com.freelance.app.domain.Tag;
import com.freelance.app.service.dto.OfferDTO;
import com.freelance.app.service.dto.TagDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Tag} and its DTO {@link TagDTO}.
 */
@Mapper(componentModel = "spring")
public interface TagMapper extends EntityMapper<TagDTO, Tag> {
    @Mapping(target = "offers", source = "offers", qualifiedByName = "offerNameSet")
    TagDTO toDto(Tag s);

    @Mapping(target = "offers", ignore = true)
    @Mapping(target = "removeOffer", ignore = true)
    Tag toEntity(TagDTO tagDTO);

    @Named("offerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OfferDTO toDtoOfferName(Offer offer);

    @Named("offerNameSet")
    default Set<OfferDTO> toDtoOfferNameSet(Set<Offer> offer) {
        return offer.stream().map(this::toDtoOfferName).collect(Collectors.toSet());
    }
}
