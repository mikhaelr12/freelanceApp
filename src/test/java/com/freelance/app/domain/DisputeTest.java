package com.freelance.app.domain;

import static com.freelance.app.domain.DisputeTestSamples.*;
import static com.freelance.app.domain.OrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DisputeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dispute.class);
        Dispute dispute1 = getDisputeSample1();
        Dispute dispute2 = new Dispute();
        assertThat(dispute1).isNotEqualTo(dispute2);

        dispute2.setId(dispute1.getId());
        assertThat(dispute1).isEqualTo(dispute2);

        dispute2 = getDisputeSample2();
        assertThat(dispute1).isNotEqualTo(dispute2);
    }

    @Test
    void orderTest() {
        Dispute dispute = getDisputeRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        dispute.setOrder(orderBack);
        assertThat(dispute.getOrder()).isEqualTo(orderBack);

        dispute.order(null);
        assertThat(dispute.getOrder()).isNull();
    }
}
