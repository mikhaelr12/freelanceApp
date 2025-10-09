package com.freelance.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RequirementDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RequirementDTO.class);
        RequirementDTO requirementDTO1 = new RequirementDTO();
        requirementDTO1.setId(1L);
        RequirementDTO requirementDTO2 = new RequirementDTO();
        assertThat(requirementDTO1).isNotEqualTo(requirementDTO2);
        requirementDTO2.setId(requirementDTO1.getId());
        assertThat(requirementDTO1).isEqualTo(requirementDTO2);
        requirementDTO2.setId(2L);
        assertThat(requirementDTO1).isNotEqualTo(requirementDTO2);
        requirementDTO1.setId(null);
        assertThat(requirementDTO1).isNotEqualTo(requirementDTO2);
    }
}
