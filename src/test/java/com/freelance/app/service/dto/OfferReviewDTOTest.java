package com.freelance.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OfferReviewDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OfferReviewDTO.class);
        OfferReviewDTO offerReviewDTO1 = new OfferReviewDTO();
        offerReviewDTO1.setId(1L);
        OfferReviewDTO offerReviewDTO2 = new OfferReviewDTO();
        assertThat(offerReviewDTO1).isNotEqualTo(offerReviewDTO2);
        offerReviewDTO2.setId(offerReviewDTO1.getId());
        assertThat(offerReviewDTO1).isEqualTo(offerReviewDTO2);
        offerReviewDTO2.setId(2L);
        assertThat(offerReviewDTO1).isNotEqualTo(offerReviewDTO2);
        offerReviewDTO1.setId(null);
        assertThat(offerReviewDTO1).isNotEqualTo(offerReviewDTO2);
    }
}
