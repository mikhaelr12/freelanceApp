package com.freelance.app.domain;

import static com.freelance.app.domain.FileObjectTestSamples.*;
import static com.freelance.app.domain.ProfileTestSamples.*;
import static com.freelance.app.domain.VerificationRequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VerificationRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VerificationRequest.class);
        VerificationRequest verificationRequest1 = getVerificationRequestSample1();
        VerificationRequest verificationRequest2 = new VerificationRequest();
        assertThat(verificationRequest1).isNotEqualTo(verificationRequest2);

        verificationRequest2.setId(verificationRequest1.getId());
        assertThat(verificationRequest1).isEqualTo(verificationRequest2);

        verificationRequest2 = getVerificationRequestSample2();
        assertThat(verificationRequest1).isNotEqualTo(verificationRequest2);
    }

    @Test
    void profileTest() {
        VerificationRequest verificationRequest = getVerificationRequestRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        verificationRequest.setProfile(profileBack);
        assertThat(verificationRequest.getProfile()).isEqualTo(profileBack);

        verificationRequest.profile(null);
        assertThat(verificationRequest.getProfile()).isNull();
    }

    @Test
    void fileObjectTest() {
        VerificationRequest verificationRequest = getVerificationRequestRandomSampleGenerator();
        FileObject fileObjectBack = getFileObjectRandomSampleGenerator();

        verificationRequest.setFileObject(fileObjectBack);
        assertThat(verificationRequest.getFileObject()).isEqualTo(fileObjectBack);

        verificationRequest.fileObject(null);
        assertThat(verificationRequest.getFileObject()).isNull();
    }
}
