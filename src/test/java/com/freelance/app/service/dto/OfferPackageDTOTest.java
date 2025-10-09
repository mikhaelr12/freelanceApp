package com.freelance.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OfferPackageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OfferPackageDTO.class);
        OfferPackageDTO offerPackageDTO1 = new OfferPackageDTO();
        offerPackageDTO1.setId(1L);
        OfferPackageDTO offerPackageDTO2 = new OfferPackageDTO();
        assertThat(offerPackageDTO1).isNotEqualTo(offerPackageDTO2);
        offerPackageDTO2.setId(offerPackageDTO1.getId());
        assertThat(offerPackageDTO1).isEqualTo(offerPackageDTO2);
        offerPackageDTO2.setId(2L);
        assertThat(offerPackageDTO1).isNotEqualTo(offerPackageDTO2);
        offerPackageDTO1.setId(null);
        assertThat(offerPackageDTO1).isNotEqualTo(offerPackageDTO2);
    }
}
