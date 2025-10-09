package com.freelance.app.domain;

import static com.freelance.app.domain.FileObjectTestSamples.*;
import static com.freelance.app.domain.ProfileTestSamples.*;
import static com.freelance.app.domain.SkillTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Profile.class);
        Profile profile1 = getProfileSample1();
        Profile profile2 = new Profile();
        assertThat(profile1).isNotEqualTo(profile2);

        profile2.setId(profile1.getId());
        assertThat(profile1).isEqualTo(profile2);

        profile2 = getProfileSample2();
        assertThat(profile1).isNotEqualTo(profile2);
    }

    @Test
    void profilePictureTest() {
        Profile profile = getProfileRandomSampleGenerator();
        FileObject fileObjectBack = getFileObjectRandomSampleGenerator();

        profile.setProfilePicture(fileObjectBack);
        assertThat(profile.getProfilePicture()).isEqualTo(fileObjectBack);

        profile.profilePicture(null);
        assertThat(profile.getProfilePicture()).isNull();
    }

    @Test
    void skillTest() {
        Profile profile = getProfileRandomSampleGenerator();
        Skill skillBack = getSkillRandomSampleGenerator();

        profile.addSkill(skillBack);
        assertThat(profile.getSkills()).containsOnly(skillBack);

        profile.removeSkill(skillBack);
        assertThat(profile.getSkills()).doesNotContain(skillBack);

        profile.skills(new HashSet<>(Set.of(skillBack)));
        assertThat(profile.getSkills()).containsOnly(skillBack);

        profile.setSkills(new HashSet<>());
        assertThat(profile.getSkills()).doesNotContain(skillBack);
    }
}
