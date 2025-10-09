package com.freelance.app.domain;

import static com.freelance.app.domain.OfferTestSamples.*;
import static com.freelance.app.domain.OfferTypeTestSamples.*;
import static com.freelance.app.domain.ProfileTestSamples.*;
import static com.freelance.app.domain.TagTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OfferTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Offer.class);
        Offer offer1 = getOfferSample1();
        Offer offer2 = new Offer();
        assertThat(offer1).isNotEqualTo(offer2);

        offer2.setId(offer1.getId());
        assertThat(offer1).isEqualTo(offer2);

        offer2 = getOfferSample2();
        assertThat(offer1).isNotEqualTo(offer2);
    }

    @Test
    void ownerTest() {
        Offer offer = getOfferRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        offer.setOwner(profileBack);
        assertThat(offer.getOwner()).isEqualTo(profileBack);

        offer.owner(null);
        assertThat(offer.getOwner()).isNull();
    }

    @Test
    void offertypeTest() {
        Offer offer = getOfferRandomSampleGenerator();
        OfferType offerTypeBack = getOfferTypeRandomSampleGenerator();

        offer.setOffertype(offerTypeBack);
        assertThat(offer.getOffertype()).isEqualTo(offerTypeBack);

        offer.offertype(null);
        assertThat(offer.getOffertype()).isNull();
    }

    @Test
    void tagTest() {
        Offer offer = getOfferRandomSampleGenerator();
        Tag tagBack = getTagRandomSampleGenerator();

        offer.addTag(tagBack);
        assertThat(offer.getTags()).containsOnly(tagBack);

        offer.removeTag(tagBack);
        assertThat(offer.getTags()).doesNotContain(tagBack);

        offer.tags(new HashSet<>(Set.of(tagBack)));
        assertThat(offer.getTags()).containsOnly(tagBack);

        offer.setTags(new HashSet<>());
        assertThat(offer.getTags()).doesNotContain(tagBack);
    }
}
