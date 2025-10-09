package com.freelance.app.service.mapper;

import com.freelance.app.domain.Category;
import com.freelance.app.domain.Profile;
import com.freelance.app.domain.Skill;
import com.freelance.app.service.dto.CategoryDTO;
import com.freelance.app.service.dto.ProfileDTO;
import com.freelance.app.service.dto.SkillDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Skill} and its DTO {@link SkillDTO}.
 */
@Mapper(componentModel = "spring")
public interface SkillMapper extends EntityMapper<SkillDTO, Skill> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryName")
    @Mapping(target = "profiles", source = "profiles", qualifiedByName = "profileIdSet")
    SkillDTO toDto(Skill s);

    @Mapping(target = "profiles", ignore = true)
    @Mapping(target = "removeProfile", ignore = true)
    Skill toEntity(SkillDTO skillDTO);

    @Named("categoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryDTO toDtoCategoryName(Category category);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);

    @Named("profileIdSet")
    default Set<ProfileDTO> toDtoProfileIdSet(Set<Profile> profile) {
        return profile.stream().map(this::toDtoProfileId).collect(Collectors.toSet());
    }
}
