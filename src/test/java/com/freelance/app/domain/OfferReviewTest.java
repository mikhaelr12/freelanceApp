package com.freelance.app.domain;

import static com.freelance.app.domain.OfferReviewTestSamples.*;
import static com.freelance.app.domain.OfferTestSamples.*;
import static com.freelance.app.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OfferReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OfferReview.class);
        OfferReview offerReview1 = getOfferReviewSample1();
        OfferReview offerReview2 = new OfferReview();
        assertThat(offerReview1).isNotEqualTo(offerReview2);

        offerReview2.setId(offerReview1.getId());
        assertThat(offerReview1).isEqualTo(offerReview2);

        offerReview2 = getOfferReviewSample2();
        assertThat(offerReview1).isNotEqualTo(offerReview2);
    }

    @Test
    void offerTest() {
        OfferReview offerReview = getOfferReviewRandomSampleGenerator();
        Offer offerBack = getOfferRandomSampleGenerator();

        offerReview.setOffer(offerBack);
        assertThat(offerReview.getOffer()).isEqualTo(offerBack);

        offerReview.offer(null);
        assertThat(offerReview.getOffer()).isNull();
    }

    @Test
    void reviewerTest() {
        OfferReview offerReview = getOfferReviewRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        offerReview.setReviewer(profileBack);
        assertThat(offerReview.getReviewer()).isEqualTo(profileBack);

        offerReview.reviewer(null);
        assertThat(offerReview.getReviewer()).isNull();
    }
}
