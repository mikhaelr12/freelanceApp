package com.freelance.app.domain;

import static com.freelance.app.domain.OrderTestSamples.*;
import static com.freelance.app.domain.RequirementTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RequirementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Requirement.class);
        Requirement requirement1 = getRequirementSample1();
        Requirement requirement2 = new Requirement();
        assertThat(requirement1).isNotEqualTo(requirement2);

        requirement2.setId(requirement1.getId());
        assertThat(requirement1).isEqualTo(requirement2);

        requirement2 = getRequirementSample2();
        assertThat(requirement1).isNotEqualTo(requirement2);
    }

    @Test
    void orderTest() {
        Requirement requirement = getRequirementRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        requirement.setOrder(orderBack);
        assertThat(requirement.getOrder()).isEqualTo(orderBack);

        requirement.order(null);
        assertThat(requirement.getOrder()).isNull();
    }
}
