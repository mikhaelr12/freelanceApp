package com.freelance.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FavoriteOfferDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FavoriteOfferDTO.class);
        FavoriteOfferDTO favoriteOfferDTO1 = new FavoriteOfferDTO();
        favoriteOfferDTO1.setId(1L);
        FavoriteOfferDTO favoriteOfferDTO2 = new FavoriteOfferDTO();
        assertThat(favoriteOfferDTO1).isNotEqualTo(favoriteOfferDTO2);
        favoriteOfferDTO2.setId(favoriteOfferDTO1.getId());
        assertThat(favoriteOfferDTO1).isEqualTo(favoriteOfferDTO2);
        favoriteOfferDTO2.setId(2L);
        assertThat(favoriteOfferDTO1).isNotEqualTo(favoriteOfferDTO2);
        favoriteOfferDTO1.setId(null);
        assertThat(favoriteOfferDTO1).isNotEqualTo(favoriteOfferDTO2);
    }
}
