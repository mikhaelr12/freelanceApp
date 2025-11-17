package com.freelance.app.service.mapper;

import com.freelance.app.domain.OfferType;
import com.freelance.app.domain.Subcategory;
import com.freelance.app.service.dto.OfferTypeDTO;
import com.freelance.app.service.dto.SubcategoryDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link OfferType} and its DTO {@link OfferTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface OfferTypeMapper extends EntityMapper<OfferTypeDTO, OfferType> {
    @Mapping(target = "subcategory", source = "subcategory", qualifiedByName = "subcategoryName")
    OfferTypeDTO toDto(OfferType s);

    @Named("subcategoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SubcategoryDTO toDtoSubcategoryName(Subcategory subcategory);
}
