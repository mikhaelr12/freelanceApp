package com.freelance.app.domain;

import static com.freelance.app.domain.DeliveryTestSamples.*;
import static com.freelance.app.domain.FileObjectTestSamples.*;
import static com.freelance.app.domain.OrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeliveryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Delivery.class);
        Delivery delivery1 = getDeliverySample1();
        Delivery delivery2 = new Delivery();
        assertThat(delivery1).isNotEqualTo(delivery2);

        delivery2.setId(delivery1.getId());
        assertThat(delivery1).isEqualTo(delivery2);

        delivery2 = getDeliverySample2();
        assertThat(delivery1).isNotEqualTo(delivery2);
    }

    @Test
    void orderTest() {
        Delivery delivery = getDeliveryRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        delivery.setOrder(orderBack);
        assertThat(delivery.getOrder()).isEqualTo(orderBack);

        delivery.order(null);
        assertThat(delivery.getOrder()).isNull();
    }

    @Test
    void fileTest() {
        Delivery delivery = getDeliveryRandomSampleGenerator();
        FileObject fileObjectBack = getFileObjectRandomSampleGenerator();

        delivery.setFile(fileObjectBack);
        assertThat(delivery.getFile()).isEqualTo(fileObjectBack);

        delivery.file(null);
        assertThat(delivery.getFile()).isNull();
    }
}
