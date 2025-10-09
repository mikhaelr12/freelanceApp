package com.freelance.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OfferMediaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OfferMediaDTO.class);
        OfferMediaDTO offerMediaDTO1 = new OfferMediaDTO();
        offerMediaDTO1.setId(1L);
        OfferMediaDTO offerMediaDTO2 = new OfferMediaDTO();
        assertThat(offerMediaDTO1).isNotEqualTo(offerMediaDTO2);
        offerMediaDTO2.setId(offerMediaDTO1.getId());
        assertThat(offerMediaDTO1).isEqualTo(offerMediaDTO2);
        offerMediaDTO2.setId(2L);
        assertThat(offerMediaDTO1).isNotEqualTo(offerMediaDTO2);
        offerMediaDTO1.setId(null);
        assertThat(offerMediaDTO1).isNotEqualTo(offerMediaDTO2);
    }
}
