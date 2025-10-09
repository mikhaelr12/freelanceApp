package com.freelance.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubcategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubcategoryDTO.class);
        SubcategoryDTO subcategoryDTO1 = new SubcategoryDTO();
        subcategoryDTO1.setId(1L);
        SubcategoryDTO subcategoryDTO2 = new SubcategoryDTO();
        assertThat(subcategoryDTO1).isNotEqualTo(subcategoryDTO2);
        subcategoryDTO2.setId(subcategoryDTO1.getId());
        assertThat(subcategoryDTO1).isEqualTo(subcategoryDTO2);
        subcategoryDTO2.setId(2L);
        assertThat(subcategoryDTO1).isNotEqualTo(subcategoryDTO2);
        subcategoryDTO1.setId(null);
        assertThat(subcategoryDTO1).isNotEqualTo(subcategoryDTO2);
    }
}
