package com.freelance.app.domain;

import static com.freelance.app.domain.OfferPackageTestSamples.*;
import static com.freelance.app.domain.OfferTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OfferPackageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OfferPackage.class);
        OfferPackage offerPackage1 = getOfferPackageSample1();
        OfferPackage offerPackage2 = new OfferPackage();
        assertThat(offerPackage1).isNotEqualTo(offerPackage2);

        offerPackage2.setId(offerPackage1.getId());
        assertThat(offerPackage1).isEqualTo(offerPackage2);

        offerPackage2 = getOfferPackageSample2();
        assertThat(offerPackage1).isNotEqualTo(offerPackage2);
    }

    @Test
    void offerTest() {
        OfferPackage offerPackage = getOfferPackageRandomSampleGenerator();
        Offer offerBack = getOfferRandomSampleGenerator();

        offerPackage.setOffer(offerBack);
        assertThat(offerPackage.getOffer()).isEqualTo(offerBack);

        offerPackage.offer(null);
        assertThat(offerPackage.getOffer()).isNull();
    }
}
