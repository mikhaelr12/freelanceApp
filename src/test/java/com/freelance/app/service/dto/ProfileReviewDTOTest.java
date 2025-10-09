package com.freelance.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfileReviewDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileReviewDTO.class);
        ProfileReviewDTO profileReviewDTO1 = new ProfileReviewDTO();
        profileReviewDTO1.setId(1L);
        ProfileReviewDTO profileReviewDTO2 = new ProfileReviewDTO();
        assertThat(profileReviewDTO1).isNotEqualTo(profileReviewDTO2);
        profileReviewDTO2.setId(profileReviewDTO1.getId());
        assertThat(profileReviewDTO1).isEqualTo(profileReviewDTO2);
        profileReviewDTO2.setId(2L);
        assertThat(profileReviewDTO1).isNotEqualTo(profileReviewDTO2);
        profileReviewDTO1.setId(null);
        assertThat(profileReviewDTO1).isNotEqualTo(profileReviewDTO2);
    }
}
