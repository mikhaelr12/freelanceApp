package com.freelance.app.service.mapper;

import com.freelance.app.domain.Profile;
import com.freelance.app.domain.ProfileReview;
import com.freelance.app.service.dto.ProfileDTO;
import com.freelance.app.service.dto.ProfileReviewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProfileReview} and its DTO {@link ProfileReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProfileReviewMapper extends EntityMapper<ProfileReviewDTO, ProfileReview> {
    @Mapping(target = "reviewer", source = "reviewer", qualifiedByName = "profileId")
    @Mapping(target = "reviewee", source = "reviewee", qualifiedByName = "profileId")
    ProfileReviewDTO toDto(ProfileReview s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
