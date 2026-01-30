package com.freelance.app.web.rest;

import static com.freelance.app.util.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.Profile;
import com.freelance.app.domain.User;
import com.freelance.app.domain.enumeration.ProfileType;
import com.freelance.app.repository.ProfileRepository;
import com.freelance.app.repository.UserRepository;
import com.freelance.app.service.dto.ProfileCreationDTO;
import com.freelance.app.service.dto.ProfileDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureWebTestClient
public class ProfileResourceIT {

    private static final String ENTITY_NAME = "profile";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Nested
    @DisplayName("Positive scenario test cases")
    @WithMockUser(username = "newlogin")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PositiveScenarios {

        @BeforeAll
        void setUp() {
            User user = createUser();
            user.setLogin("newlogin");
            user.setEmail("newmail@mail.com");
            userRepository.save(user).block();
        }

        @Test
        @DisplayName("Should successfully create a profile and return the entity")
        void createProfileSuccessfully() {
            Profile responseBody = webTestClient
                .post()
                .uri(PROFILE_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createProfileCreationDTO())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Profile.class)
                .returnResult()
                .getResponseBody();

            assertThat(responseBody).isNotNull();
            assertThat(responseBody.getUser().getLogin()).isEqualTo("testuser");
            profileRepository.deleteAll().block();
        }

        @Test
        @DisplayName("Should update a profile")
        void testUpdateProfile() {
            profileRepository.findAll().collectList().block();
            Profile profile = webTestClient
                .post()
                .uri(PROFILE_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createProfileCreationDTO())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Profile.class)
                .returnResult()
                .getResponseBody();

            assertThat(profile).isNotNull();

            Profile responseBody = webTestClient
                .put()
                .uri(PROFILE_ID_API_URL, profile.getId())
                .bodyValue(createProfileEditDTO())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Profile.class)
                .returnResult()
                .getResponseBody();

            assertThat(responseBody).isNotNull();
            assertThat(responseBody.getFirstName()).isEqualTo("newFirstName");

            profileRepository.deleteAll().block();
        }

        @Test
        @DisplayName("Should return profile by id")
        void testGetProfileById() {
            Profile profile = webTestClient
                .post()
                .uri(PROFILE_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createProfileCreationDTO())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Profile.class)
                .returnResult()
                .getResponseBody();

            assertThat(profile).isNotNull();

            ProfileDTO profileDTO = webTestClient
                .get()
                .uri(PROFILE_ID_API_URL, profile.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProfileDTO.class)
                .returnResult()
                .getResponseBody();

            assertThat(profileDTO).isNotNull();
            assertThat(profileDTO.getId()).isEqualTo(profile.getId());

            profileRepository.deleteAll().block();
        }

        @Test
        @DisplayName("Should delete profile by id")
        void testDeleteProfileById() {
            Profile profile = webTestClient
                .post()
                .uri(PROFILE_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createProfileCreationDTO())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Profile.class)
                .returnResult()
                .getResponseBody();

            assertThat(profile).isNotNull();

            webTestClient
                .delete()
                .uri(PROFILE_ID_API_URL, profile.getId())
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectHeader()
                .valueMatches(getAlertHeader(), "A " + ENTITY_NAME + " is deleted with identifier " + profile.getId());

            assertThat(profileRepository.count().block()).isZero();
        }
    }

    @Nested
    @DisplayName("Negative scenario test cases")
    class NegativeScenarios {

        @Test
        @DisplayName("Should return 400 (BAD REQUEST) if user not found in repository")
        @WithMockUser(username = "testUser")
        void testCreateProfileUserNotFound() {
            webTestClient
                .post()
                .uri(PROFILE_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createProfileCreationDTO())
                .exchange()
                .expectStatus()
                .isBadRequest();
        }

        @Test
        @DisplayName("Should return 401 (UNAUTHORIZED) if user not logged in")
        void testCreateProfileUserNotLoggedIn() {
            webTestClient
                .post()
                .uri(PROFILE_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createProfileCreationDTO())
                .exchange()
                .expectStatus()
                .isUnauthorized();
        }

        @Test
        @DisplayName("Should return 400 (BAD REQUEST) when providing invalid profile creation data")
        @WithMockUser
        void testCreateProfileInvalidData() {
            webTestClient
                .post()
                .uri(PROFILE_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ProfileCreationDTO("", "lastName", "description", ProfileType.CLIENT, null))
                .exchange()
                .expectStatus()
                .isBadRequest();
        }

        @Test
        @DisplayName("Should return 404 (NOT FOUND) for non existing profile")
        @WithMockUser
        void testUpdateNonExistingProfile() {
            webTestClient
                .put()
                .uri(PROFILE_ID_API_URL, Long.MAX_VALUE)
                .bodyValue(createProfileEditDTO())
                .exchange()
                .expectStatus()
                .isNotFound();
        }

        @Test
        @DisplayName("Should return 401 (UNAUTHORIZED) for update profile with non logged user")
        void testUpdateProfileNotLoggedIn() {
            webTestClient
                .put()
                .uri(PROFILE_ID_API_URL, Long.MAX_VALUE)
                .bodyValue(createProfileEditDTO())
                .exchange()
                .expectStatus()
                .isUnauthorized();
        }

        @Test
        @DisplayName("Should return 400 (BAD REQUEST) for update with id type mismatch")
        @WithMockUser
        void testUpdateProfileIdTypeMismatch() {
            webTestClient
                .put()
                .uri(PROFILE_ID_API_URL, "invalid-id")
                .bodyValue(createProfileEditDTO())
                .exchange()
                .expectStatus()
                .isBadRequest();
        }

        @Test
        @DisplayName("Should return 404 (NOT FOUND) on get profile by id")
        @WithMockUser
        void testGetProfileByIdNotFound() {
            webTestClient.get().uri(PROFILE_ID_API_URL, Long.MAX_VALUE).exchange().expectStatus().isNotFound();
        }

        @Test
        @DisplayName("Should return 400 (BAD REQUEST) for get profile by id type mismatch")
        @WithMockUser
        void testGetProfileByIdTypeMismatch() {
            webTestClient.get().uri(PROFILE_ID_API_URL, "invalid-id").exchange().expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Should return 404 (NOT FOUND) for deleting non existing profile")
        @WithMockUser
        void testDeleteNonExistingProfile() {
            webTestClient.delete().uri(PROFILE_ID_API_URL, Long.MAX_VALUE).exchange().expectStatus().isNotFound();
        }

        @Test
        @DisplayName("Should return 403 (FORBIDDEN) for deleting a profile not belonging to current user")
        @WithMockUser
        void testDeleteProfileNotBelongingToCurrentUser() {
            User user = createUser();
            user.setLogin("some-login");
            user.setEmail("somemail@mail.com");
            userRepository.save(user).block();
            Profile profile = profileRepository.save(createProfile(user)).block();

            Assertions.assertNotNull(profile);
            webTestClient.delete().uri(PROFILE_ID_API_URL, profile.getId()).exchange().expectStatus().isForbidden();

            Assertions.assertNotNull(user);
            profileRepository.delete(profile).block();
            userRepository.delete(user).block();
        }
    }
}
