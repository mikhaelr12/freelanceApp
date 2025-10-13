package com.freelance.app.service.mapper;

import com.freelance.app.domain.Category;
import com.freelance.app.domain.OfferMedia;
import com.freelance.app.service.dto.CategoryDTO;
import com.freelance.app.service.dto.OfferMediaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {
    @BeanMapping(ignoreByDefault = true)
    Category toEntity(CategoryDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    void partialUpdate(@MappingTarget Category entity, CategoryDTO dto);
}
