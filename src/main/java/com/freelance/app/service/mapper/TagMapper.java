package com.freelance.app.service.mapper;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.Offer;
import com.freelance.app.domain.Tag;
import com.freelance.app.service.dto.FavoriteOfferDTO;
import com.freelance.app.service.dto.OfferDTO;
import com.freelance.app.service.dto.TagDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tag} and its DTO {@link TagDTO}.
 */
@Mapper(componentModel = "spring")
public interface TagMapper extends EntityMapper<TagDTO, Tag> {
    @Mapping(target = "offers", source = "offers", qualifiedByName = "offerNameSet")
    TagDTO toDto(Tag s);

    @BeanMapping(ignoreByDefault = true)
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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    void partialUpdate(@MappingTarget Tag entity, TagDTO dto);
}
