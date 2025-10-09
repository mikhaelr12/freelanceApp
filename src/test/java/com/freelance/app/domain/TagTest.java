package com.freelance.app.domain;

import static com.freelance.app.domain.OfferTestSamples.*;
import static com.freelance.app.domain.TagTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TagTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tag.class);
        Tag tag1 = getTagSample1();
        Tag tag2 = new Tag();
        assertThat(tag1).isNotEqualTo(tag2);

        tag2.setId(tag1.getId());
        assertThat(tag1).isEqualTo(tag2);

        tag2 = getTagSample2();
        assertThat(tag1).isNotEqualTo(tag2);
    }

    @Test
    void offerTest() {
        Tag tag = getTagRandomSampleGenerator();
        Offer offerBack = getOfferRandomSampleGenerator();

        tag.addOffer(offerBack);
        assertThat(tag.getOffers()).containsOnly(offerBack);
        assertThat(offerBack.getTags()).containsOnly(tag);

        tag.removeOffer(offerBack);
        assertThat(tag.getOffers()).doesNotContain(offerBack);
        assertThat(offerBack.getTags()).doesNotContain(tag);

        tag.offers(new HashSet<>(Set.of(offerBack)));
        assertThat(tag.getOffers()).containsOnly(offerBack);
        assertThat(offerBack.getTags()).containsOnly(tag);

        tag.setOffers(new HashSet<>());
        assertThat(tag.getOffers()).doesNotContain(offerBack);
        assertThat(offerBack.getTags()).doesNotContain(tag);
    }
}
