package com.freelance.app.domain;

import static com.freelance.app.domain.CategoryTestSamples.*;
import static com.freelance.app.domain.ProfileTestSamples.*;
import static com.freelance.app.domain.SkillTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SkillTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Skill.class);
        Skill skill1 = getSkillSample1();
        Skill skill2 = new Skill();
        assertThat(skill1).isNotEqualTo(skill2);

        skill2.setId(skill1.getId());
        assertThat(skill1).isEqualTo(skill2);

        skill2 = getSkillSample2();
        assertThat(skill1).isNotEqualTo(skill2);
    }

    @Test
    void categoryTest() {
        Skill skill = getSkillRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        skill.setCategory(categoryBack);
        assertThat(skill.getCategory()).isEqualTo(categoryBack);

        skill.category(null);
        assertThat(skill.getCategory()).isNull();
    }

    @Test
    void profileTest() {
        Skill skill = getSkillRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        skill.addProfile(profileBack);
        assertThat(skill.getProfiles()).containsOnly(profileBack);
        assertThat(profileBack.getSkills()).containsOnly(skill);

        skill.removeProfile(profileBack);
        assertThat(skill.getProfiles()).doesNotContain(profileBack);
        assertThat(profileBack.getSkills()).doesNotContain(skill);

        skill.profiles(new HashSet<>(Set.of(profileBack)));
        assertThat(skill.getProfiles()).containsOnly(profileBack);
        assertThat(profileBack.getSkills()).containsOnly(skill);

        skill.setProfiles(new HashSet<>());
        assertThat(skill.getProfiles()).doesNotContain(profileBack);
        assertThat(profileBack.getSkills()).doesNotContain(skill);
    }
}
