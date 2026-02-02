package com.freelance.app.web.rest;

import static com.freelance.app.util.TestUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.*;
import com.freelance.app.repository.*;
import com.freelance.app.service.dto.FavoriteOfferDTO;
import com.freelance.app.util.ProfileHelper;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@IntegrationTest
@AutoConfigureWebTestClient
public class FavoriteOfferResourceIT {

    @Autowired
    private FavoriteOfferRepository favoriteOfferRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private OfferTypeRepository offerTypeRepository;

    @Autowired
    private ProfileHelper profileHelper;

    private Offer offer;
    private Profile profile;
    private User user;

    @BeforeAll
    void beforeAll() {
        createEntities();
    }

    @AfterAll
    void afterAll() {
        offerRepository.delete(offer).block();
        profileRepository.delete(profile).block();
        userRepository.delete(user).block();
    }

    @Nested
    @DisplayName("Tests for (POST) create favorite offer endpoint")
    @Order(1)
    class FavoriteOfferCreationTests {

        @Test
        @DisplayName("Should create a favorite offer")
        @WithMockUser(username = "testuser")
        void testCreateFavoriteOffer() {
            FavoriteOffer responseBody = webTestClient
                .post()
                .uri(FAVORITE_OFFER_ID_API_URL, offer.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(FavoriteOffer.class)
                .returnResult()
                .getResponseBody();

            Assertions.assertNotNull(responseBody);
            assertThat(responseBody.getOfferId()).isEqualTo(offer.getId());
            favoriteOfferRepository.delete(responseBody).block();
        }

        @Test
        @DisplayName("Should return 400 (BAD REQUEST) for id type mismatch")
        @WithMockUser
        void testCreateFavoriteOfferIdTypeMismatch() {
            webTestClient.post().uri(FAVORITE_OFFER_ID_API_URL, "invalid-id").exchange().expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Should return 404 (NOT FOUND) if user without profile wants to add favorite offer")
        @WithMockUser(username = "user-without-profile")
        void testCreateFavoriteOfferWithoutProfile() {
            webTestClient.post().uri(FAVORITE_OFFER_ID_API_URL, offer.getId()).exchange().expectStatus().isNotFound();
        }

        @Test
        @DisplayName("Should return 404 (NOT FOUND) for trying to add non existing offer to favorites")
        @WithMockUser(username = "testuser")
        void testCreateFavoriteOfferNotFound() {
            webTestClient.post().uri(FAVORITE_OFFER_ID_API_URL, Long.MAX_VALUE).exchange().expectStatus().isNotFound();
        }
    }

    @Nested
    @DisplayName("Tests for (GET) get all favorite offer endpoint")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Order(2)
    class GetFavoriteOffersTests {

        @BeforeAll
        void beforeAll() {
            favoriteOfferRepository.save(createFavoriteOffer(profile, offer)).block();
        }

        @Test
        @DisplayName("Should return all favorite offers")
        @Order(1)
        @WithMockUser(username = "testuser")
        void testGetAllFavoriteOffers() {
            List<FavoriteOfferDTO> responseBody = webTestClient
                .get()
                .uri(FAVORITE_OFFER_API_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(FavoriteOfferDTO.class)
                .returnResult()
                .getResponseBody();

            assertThat(responseBody).isNotNull();

            Assertions.assertNotNull(responseBody);
            assertThat(responseBody.size()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Should return empty list with 200 (OK)")
        @Order(2)
        @WithMockUser(username = "testuser")
        void testGetAllFavoriteOffersEmpty() {
            favoriteOfferRepository.deleteAll().block();
            List<FavoriteOfferDTO> responseBody = webTestClient
                .get()
                .uri(FAVORITE_OFFER_API_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(FavoriteOfferDTO.class)
                .returnResult()
                .getResponseBody();

            Assertions.assertNotNull(responseBody);
            assertThat(responseBody).isNotNull();
            assertThat(responseBody.size()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Tests for (DELETE) delete a favorite offer")
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteFavoriteOfferTests {

        @Test
        @DisplayName("Should delete a favorite offer")
        @WithMockUser(username = "testuser")
        void testDeleteFavoriteOffer() {
            Profile current = profileHelper.getCurrentProfile().block();

            FavoriteOffer fo = favoriteOfferRepository.save(createFavoriteOffer(current, offer)).block();

            webTestClient.delete().uri(FAVORITE_OFFER_REMOVE_API_URL, Objects.requireNonNull(fo).getId()).exchange().expectStatus().isOk();

            favoriteOfferRepository.delete(fo).block();
        }

        @Test
        @DisplayName("Should return 400 (BAD REQUEST) for id type mismatch")
        @WithMockUser(username = "testuser")
        void testDeleteFavoriteOfferIdTypeMismatch() {
            webTestClient.delete().uri(FAVORITE_OFFER_REMOVE_API_URL, "invalid-id").exchange().expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Should return 404 (NOT FOUND) for favorite offer not found")
        @WithMockUser(username = "testuser")
        void testDeleteFavoriteOfferNotFound() {
            webTestClient.delete().uri(FAVORITE_OFFER_REMOVE_API_URL, Long.MAX_VALUE).exchange().expectStatus().isNotFound();
        }

        @Test
        @DisplayName("Should return 403 (FORBIDDEN) for profile deleting a favorite offer not belonging to it")
        @WithMockUser(username = "anotheruser")
        void testDeleteFavoriteOfferNotBelongingToProfile() {
            User user = new User();
            user.setLogin("testlogin");
            user.setPassword("testpassword");
            user.setEmail("testemail@test@.com");
            user.setCreatedBy("testuser");
            user.setCreatedDate(Instant.now());
            userRepository.save(user).block();

            User anotherUser = createUser();
            anotherUser.setLogin("anotheruser");
            anotherUser.setEmail("anothermail@gmail.com");
            userRepository.save(anotherUser).block();

            Profile profileWithFO = profileRepository.save(createProfile(user)).block();

            FavoriteOffer fo = favoriteOfferRepository.save(createFavoriteOffer(profileWithFO, offer)).block();

            Profile anotherProfile = profileRepository
                .save(new Profile().firstName("test-first").lastName("test-last").user(anotherUser))
                .block();

            System.out.println("fos: " + favoriteOfferRepository.findAll().collectList().block());

            Assertions.assertNotNull(fo);

            webTestClient.delete().uri(FAVORITE_OFFER_REMOVE_API_URL, fo.getId()).exchange().expectStatus().isForbidden();

            Assertions.assertNotNull(anotherProfile);
            Assertions.assertNotNull(profileWithFO);
            profileRepository.delete(profileWithFO).block();
            profileRepository.delete(anotherProfile).block();
            userRepository.delete(anotherUser).block();
            userRepository.delete(user).block();
        }
    }

    void createEntities() {
        user = userRepository.save(createUser()).block();
        profile = profileRepository.save(createProfile(user)).block();
        OfferType offerType = offerTypeRepository.findAll().blockFirst();
        offer = offerRepository.save(createOffer(profile, offerType)).block();
    }
}
