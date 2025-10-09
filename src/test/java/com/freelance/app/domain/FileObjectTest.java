package com.freelance.app.domain;

import static com.freelance.app.domain.FileObjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FileObjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileObject.class);
        FileObject fileObject1 = getFileObjectSample1();
        FileObject fileObject2 = new FileObject();
        assertThat(fileObject1).isNotEqualTo(fileObject2);

        fileObject2.setId(fileObject1.getId());
        assertThat(fileObject1).isEqualTo(fileObject2);

        fileObject2 = getFileObjectSample2();
        assertThat(fileObject1).isNotEqualTo(fileObject2);
    }
}
