package com.freelance.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FileObjectDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileObjectDTO.class);
        FileObjectDTO fileObjectDTO1 = new FileObjectDTO();
        fileObjectDTO1.setId(1L);
        FileObjectDTO fileObjectDTO2 = new FileObjectDTO();
        assertThat(fileObjectDTO1).isNotEqualTo(fileObjectDTO2);
        fileObjectDTO2.setId(fileObjectDTO1.getId());
        assertThat(fileObjectDTO1).isEqualTo(fileObjectDTO2);
        fileObjectDTO2.setId(2L);
        assertThat(fileObjectDTO1).isNotEqualTo(fileObjectDTO2);
        fileObjectDTO1.setId(null);
        assertThat(fileObjectDTO1).isNotEqualTo(fileObjectDTO2);
    }
}
