package com.freelance.app.domain;

import static com.freelance.app.domain.ProfileReviewTestSamples.*;
import static com.freelance.app.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfileReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileReview.class);
        ProfileReview profileReview1 = getProfileReviewSample1();
        ProfileReview profileReview2 = new ProfileReview();
        assertThat(profileReview1).isNotEqualTo(profileReview2);

        profileReview2.setId(profileReview1.getId());
        assertThat(profileReview1).isEqualTo(profileReview2);

        profileReview2 = getProfileReviewSample2();
        assertThat(profileReview1).isNotEqualTo(profileReview2);
    }

    @Test
    void reviewerTest() {
        ProfileReview profileReview = getProfileReviewRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        profileReview.setReviewer(profileBack);
        assertThat(profileReview.getReviewer()).isEqualTo(profileBack);

        profileReview.reviewer(null);
        assertThat(profileReview.getReviewer()).isNull();
    }

    @Test
    void revieweeTest() {
        ProfileReview profileReview = getProfileReviewRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        profileReview.setReviewee(profileBack);
        assertThat(profileReview.getReviewee()).isEqualTo(profileBack);

        profileReview.reviewee(null);
        assertThat(profileReview.getReviewee()).isNull();
    }
}
