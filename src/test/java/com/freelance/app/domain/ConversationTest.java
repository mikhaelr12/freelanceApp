package com.freelance.app.domain;

import static com.freelance.app.domain.ConversationTestSamples.*;
import static com.freelance.app.domain.OrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConversationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Conversation.class);
        Conversation conversation1 = getConversationSample1();
        Conversation conversation2 = new Conversation();
        assertThat(conversation1).isNotEqualTo(conversation2);

        conversation2.setId(conversation1.getId());
        assertThat(conversation1).isEqualTo(conversation2);

        conversation2 = getConversationSample2();
        assertThat(conversation1).isNotEqualTo(conversation2);
    }

    @Test
    void orderTest() {
        Conversation conversation = getConversationRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        conversation.setOrder(orderBack);
        assertThat(conversation.getOrder()).isEqualTo(orderBack);

        conversation.order(null);
        assertThat(conversation.getOrder()).isNull();
    }
}
