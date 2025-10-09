package com.freelance.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OfferTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OfferTypeDTO.class);
        OfferTypeDTO offerTypeDTO1 = new OfferTypeDTO();
        offerTypeDTO1.setId(1L);
        OfferTypeDTO offerTypeDTO2 = new OfferTypeDTO();
        assertThat(offerTypeDTO1).isNotEqualTo(offerTypeDTO2);
        offerTypeDTO2.setId(offerTypeDTO1.getId());
        assertThat(offerTypeDTO1).isEqualTo(offerTypeDTO2);
        offerTypeDTO2.setId(2L);
        assertThat(offerTypeDTO1).isNotEqualTo(offerTypeDTO2);
        offerTypeDTO1.setId(null);
        assertThat(offerTypeDTO1).isNotEqualTo(offerTypeDTO2);
    }
}
