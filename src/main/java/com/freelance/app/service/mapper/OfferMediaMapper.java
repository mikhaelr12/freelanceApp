package com.freelance.app.service.mapper;

import com.freelance.app.domain.FileObject;
import com.freelance.app.domain.Offer;
import com.freelance.app.domain.OfferMedia;
import com.freelance.app.service.dto.FileObjectDTO;
import com.freelance.app.service.dto.OfferDTO;
import com.freelance.app.service.dto.OfferMediaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OfferMedia} and its DTO {@link OfferMediaDTO}.
 */
@Mapper(componentModel = "spring")
public interface OfferMediaMapper extends EntityMapper<OfferMediaDTO, OfferMedia> {
    @Mapping(target = "offer", source = "offer", qualifiedByName = "offerName")
    @Mapping(target = "file", source = "file", qualifiedByName = "fileObjectObjectKey")
    OfferMediaDTO toDto(OfferMedia s);

    @Named("offerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OfferDTO toDtoOfferName(Offer offer);

    @Named("fileObjectObjectKey")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "objectKey", source = "objectKey")
    FileObjectDTO toDtoFileObjectObjectKey(FileObject fileObject);

    @BeanMapping(ignoreByDefault = true)
    OfferMedia toEntity(OfferMediaDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    void partialUpdate(@MappingTarget OfferMedia entity, OfferMediaDTO dto);
}
