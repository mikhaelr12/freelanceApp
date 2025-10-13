package com.freelance.app.service.mapper;

import com.freelance.app.domain.*;
import com.freelance.app.service.dto.*;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Profile} and its DTO {@link ProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProfileMapper extends EntityMapper<ProfileDTO, Profile> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "profilePicture", source = "profilePicture", qualifiedByName = "fileObjectObjectKey")
    @Mapping(target = "skills", source = "skills", qualifiedByName = "skillIdSet")
    ProfileDTO toDto(Profile s);

    @Mapping(target = "removeSkill", ignore = true)
    Profile toEntity(ProfileDTO profileDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("fileObjectObjectKey")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "objectKey", source = "objectKey")
    FileObjectDTO toDtoFileObjectObjectKey(FileObject fileObject);

    @Named("skillId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SkillDTO toDtoSkillId(Skill skill);

    @Named("skillIdSet")
    default Set<SkillDTO> toDtoSkillIdSet(Set<Skill> skill) {
        return skill.stream().map(this::toDtoSkillId).collect(Collectors.toSet());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    void partialUpdate(@MappingTarget Profile entity, ProfileDTO dto);
}
