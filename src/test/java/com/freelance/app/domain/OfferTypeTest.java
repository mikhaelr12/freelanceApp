package com.freelance.app.domain;

import static com.freelance.app.domain.OfferTypeTestSamples.*;
import static com.freelance.app.domain.SubcategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OfferTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OfferType.class);
        OfferType offerType1 = getOfferTypeSample1();
        OfferType offerType2 = new OfferType();
        assertThat(offerType1).isNotEqualTo(offerType2);

        offerType2.setId(offerType1.getId());
        assertThat(offerType1).isEqualTo(offerType2);

        offerType2 = getOfferTypeSample2();
        assertThat(offerType1).isNotEqualTo(offerType2);
    }

    @Test
    void subcategoryTest() {
        OfferType offerType = getOfferTypeRandomSampleGenerator();
        Subcategory subcategoryBack = getSubcategoryRandomSampleGenerator();

        offerType.setSubcategory(subcategoryBack);
        assertThat(offerType.getSubcategory()).isEqualTo(subcategoryBack);

        offerType.subcategory(null);
        assertThat(offerType.getSubcategory()).isNull();
    }
}
