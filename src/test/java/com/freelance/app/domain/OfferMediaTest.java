package com.freelance.app.domain;

import static com.freelance.app.domain.FileObjectTestSamples.*;
import static com.freelance.app.domain.OfferMediaTestSamples.*;
import static com.freelance.app.domain.OfferTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OfferMediaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OfferMedia.class);
        OfferMedia offerMedia1 = getOfferMediaSample1();
        OfferMedia offerMedia2 = new OfferMedia();
        assertThat(offerMedia1).isNotEqualTo(offerMedia2);

        offerMedia2.setId(offerMedia1.getId());
        assertThat(offerMedia1).isEqualTo(offerMedia2);

        offerMedia2 = getOfferMediaSample2();
        assertThat(offerMedia1).isNotEqualTo(offerMedia2);
    }

    @Test
    void offerTest() {
        OfferMedia offerMedia = getOfferMediaRandomSampleGenerator();
        Offer offerBack = getOfferRandomSampleGenerator();

        offerMedia.setOffer(offerBack);
        assertThat(offerMedia.getOffer()).isEqualTo(offerBack);

        offerMedia.offer(null);
        assertThat(offerMedia.getOffer()).isNull();
    }

    @Test
    void fileTest() {
        OfferMedia offerMedia = getOfferMediaRandomSampleGenerator();
        FileObject fileObjectBack = getFileObjectRandomSampleGenerator();

        offerMedia.setFile(fileObjectBack);
        assertThat(offerMedia.getFile()).isEqualTo(fileObjectBack);

        offerMedia.file(null);
        assertThat(offerMedia.getFile()).isNull();
    }
}
