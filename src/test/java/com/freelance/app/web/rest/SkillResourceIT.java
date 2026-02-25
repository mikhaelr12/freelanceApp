package com.freelance.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.IntegrationTest;
import com.freelance.app.service.dto.SkillShortDTO;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class SkillResourceIT {

    private static final String SKILLS_API_URL = "/api/skills";
    private static final Long WRITING_CATEGORY_ID = 1001L;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("GET /api/skills/all should return all skills")
    void getAllSkillsShouldReturnSeededSkills() {
        List<SkillShortDTO> responseBody = webTestClient
            .get()
            .uri(SKILLS_API_URL + "/all")
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(SkillShortDTO.class)
            .returnResult()
            .getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody).isNotEmpty();
        assertThat(responseBody).extracting(SkillShortDTO::name).contains("Core Writing & Translation");
    }

    @Test
    @DisplayName("GET /api/skills/category/{categoryId} should return category scoped skills")
    void getAllSkillsForCategoryShouldReturnOnlyMatchingCategory() {
        List<SkillShortDTO> responseBody = webTestClient
            .get()
            .uri(SKILLS_API_URL + "/category/{categoryId}", WRITING_CATEGORY_ID)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(SkillShortDTO.class)
            .returnResult()
            .getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody).isNotEmpty();
        assertThat(responseBody)
            .extracting(SkillShortDTO::name)
            .contains("Core Writing & Translation", "Advanced Writing & Translation")
            .doesNotContain("Core Digital Marketing");
    }
}
