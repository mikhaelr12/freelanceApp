package com.freelance.app.domain;

import static com.freelance.app.domain.FavoriteOfferTestSamples.*;
import static com.freelance.app.domain.OfferTestSamples.*;
import static com.freelance.app.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FavoriteOfferTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FavoriteOffer.class);
        FavoriteOffer favoriteOffer1 = getFavoriteOfferSample1();
        FavoriteOffer favoriteOffer2 = new FavoriteOffer();
        assertThat(favoriteOffer1).isNotEqualTo(favoriteOffer2);

        favoriteOffer2.setId(favoriteOffer1.getId());
        assertThat(favoriteOffer1).isEqualTo(favoriteOffer2);

        favoriteOffer2 = getFavoriteOfferSample2();
        assertThat(favoriteOffer1).isNotEqualTo(favoriteOffer2);
    }

    @Test
    void profileTest() {
        FavoriteOffer favoriteOffer = getFavoriteOfferRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        favoriteOffer.setProfile(profileBack);
        assertThat(favoriteOffer.getProfile()).isEqualTo(profileBack);

        favoriteOffer.profile(null);
        assertThat(favoriteOffer.getProfile()).isNull();
    }

    @Test
    void offerTest() {
        FavoriteOffer favoriteOffer = getFavoriteOfferRandomSampleGenerator();
        Offer offerBack = getOfferRandomSampleGenerator();

        favoriteOffer.setOffer(offerBack);
        assertThat(favoriteOffer.getOffer()).isEqualTo(offerBack);

        favoriteOffer.offer(null);
        assertThat(favoriteOffer.getOffer()).isNull();
    }
}
