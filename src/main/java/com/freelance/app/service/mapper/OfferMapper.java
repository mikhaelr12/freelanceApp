package com.freelance.app.service.mapper;

import com.freelance.app.domain.Offer;
import com.freelance.app.domain.OfferType;
import com.freelance.app.domain.Profile;
import com.freelance.app.domain.Tag;
import com.freelance.app.service.dto.OfferDTO;
import com.freelance.app.service.dto.OfferTypeDTO;
import com.freelance.app.service.dto.ProfileDTO;
import com.freelance.app.service.dto.TagDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Offer} and its DTO {@link OfferDTO}.
 */
@Mapper(componentModel = "spring")
public interface OfferMapper extends EntityMapper<OfferDTO, Offer> {
    @Mapping(target = "owner", source = "owner", qualifiedByName = "profileId")
    @Mapping(target = "offertype", source = "offertype", qualifiedByName = "offerTypeName")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagNameSet")
    OfferDTO toDto(Offer s);

    @BeanMapping(ignoreByDefault = true)
    Offer toEntity(OfferDTO offerDTO);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);

    @Named("offerTypeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OfferTypeDTO toDtoOfferTypeName(OfferType offerType);

    @Named("tagName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TagDTO toDtoTagName(Tag tag);

    @Named("tagNameSet")
    default Set<TagDTO> toDtoTagNameSet(Set<Tag> tag) {
        return tag.stream().map(this::toDtoTagName).collect(Collectors.toSet());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    void partialUpdate(@MappingTarget Offer entity, OfferDTO dto);
}
