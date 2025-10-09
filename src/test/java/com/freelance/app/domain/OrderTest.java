package com.freelance.app.domain;

import static com.freelance.app.domain.OfferPackageTestSamples.*;
import static com.freelance.app.domain.OrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Order.class);
        Order order1 = getOrderSample1();
        Order order2 = new Order();
        assertThat(order1).isNotEqualTo(order2);

        order2.setId(order1.getId());
        assertThat(order1).isEqualTo(order2);

        order2 = getOrderSample2();
        assertThat(order1).isNotEqualTo(order2);
    }

    @Test
    void offerpackageTest() {
        Order order = getOrderRandomSampleGenerator();
        OfferPackage offerPackageBack = getOfferPackageRandomSampleGenerator();

        order.setOfferpackage(offerPackageBack);
        assertThat(order.getOfferpackage()).isEqualTo(offerPackageBack);

        order.offerpackage(null);
        assertThat(order.getOfferpackage()).isNull();
    }
}
