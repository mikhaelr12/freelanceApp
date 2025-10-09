package com.freelance.app.service.mapper;

import com.freelance.app.domain.Category;
import com.freelance.app.domain.Subcategory;
import com.freelance.app.service.dto.CategoryDTO;
import com.freelance.app.service.dto.SubcategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Subcategory} and its DTO {@link SubcategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubcategoryMapper extends EntityMapper<SubcategoryDTO, Subcategory> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryName")
    SubcategoryDTO toDto(Subcategory s);

    @Named("categoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryDTO toDtoCategoryName(Category category);
}
