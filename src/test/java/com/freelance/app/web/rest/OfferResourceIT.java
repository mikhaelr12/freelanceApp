package com.freelance.app.web.rest;

import static com.freelance.app.util.TestUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.*;
import com.freelance.app.domain.enumeration.OfferStatus;
import com.freelance.app.repository.*;
import com.freelance.app.service.dto.OfferDTO;
import com.freelance.app.service.dto.OfferShortDTO;
import com.freelance.app.service.dto.OfferUpdateDTO;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser(username = "newlogin")
public class OfferResourceIT {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private OfferTypeRepository offerTypeRepository;

    @Autowired
    private WebTestClient webTestClient;

    private User user;
    private Profile profile;
    private Offer existingOffer;
    private OfferType offerType;

    @BeforeAll
    void setUp() {
        offerType = offerTypeRepository.findAll().blockFirst();
        user = createUser();
        user.setLogin("newlogin");
        user.setEmail("newmail@mail.com");
        userRepository.save(user).block();

        profile = createProfile(user);
        profileRepository.save(profile).block();

        existingOffer = offerRepository
            .save(
                new Offer()
                    .name("testName")
                    .description("testDescription")
                    .owner(profile)
                    .status(OfferStatus.ACTIVE)
                    .visibility(true)
                    .offertype(offerType)
            )
            .block();
    }

    @AfterAll
    void tearDown() {
        offerRepository.delete(existingOffer).block();
        profileRepository.delete(profile).block();
        userRepository.delete(user).block();
    }

    @Nested
    @DisplayName("(GET)Get all offers endpoint tests")
    class GetAllOffersEndpointTests {

        @Test
        @DisplayName("Should return 200 (OK) with all offers")
        void testGetAllOffers() {
            List<OfferShortDTO> responseBody = webTestClient
                .get()
                .uri(OFFER_API_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectHeader()
                .exists(X_TOTAL_COUNT)
                .expectBodyList(OfferShortDTO.class)
                .returnResult()
                .getResponseBody();

            Assertions.assertNotNull(responseBody);
            assertThat(responseBody.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should return 200 (OK) with offer types filtered by criteria")
        void testGetAllOfferTypeFilteredByCriteria() {
            OfferType offerType = offerTypeRepository.save(new OfferType().name("backend").active(true)).block();
            Assertions.assertNotNull(offerType);
            List<Offer> tempOffers = createTempOffers(offerType, 10, profile);
            offerRepository.saveAll(tempOffers).collectList().block();

            List<OfferShortDTO> responseBody = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(OFFER_API_URL).queryParam("offertypeId.equals", offerType.getId()).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectHeader()
                .exists(X_TOTAL_COUNT)
                .expectBodyList(OfferShortDTO.class)
                .returnResult()
                .getResponseBody();

            Assertions.assertNotNull(responseBody);
            assertThat(responseBody.size()).isEqualTo(10);

            offerRepository.deleteAll(tempOffers).block();
            offerTypeRepository.delete(offerType).block();
        }

        @Test
        @DisplayName("Should return 200 (OK) with offer types paginated")
        void testGetAllOfferTypePaginated() {
            OfferType offerType = offerTypeRepository.save(new OfferType().name("backend").active(true)).block();
            Assertions.assertNotNull(offerType);

            List<Offer> tempOffers = createTempOffers(offerType, 20, profile);
            offerRepository.saveAll(tempOffers).collectList().block();

            List<OfferShortDTO> responseBody = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(OFFER_API_URL).queryParam("page", "0").queryParam("size", "10").build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectHeader()
                .exists(X_TOTAL_COUNT)
                .expectBodyList(OfferShortDTO.class)
                .returnResult()
                .getResponseBody();

            Assertions.assertNotNull(responseBody);
            assertThat(responseBody.size()).isEqualTo(10);

            offerRepository.deleteAll(tempOffers).block();
            offerTypeRepository.delete(offerType).block();
        }

        @Test
        @DisplayName("Should return 200 (OK) with empty list for non existing offer type by criteria")
        void testGetAllOfferTypePaginatedByCriteriaNotFound() {
            List<OfferShortDTO> responseBody = webTestClient
                .get()
                .uri(uriBuilder ->
                    uriBuilder
                        .path(OFFER_API_URL)
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .queryParam("name.contains", "non-existing-name")
                        .build()
                )
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectHeader()
                .exists(X_TOTAL_COUNT)
                .expectBodyList(OfferShortDTO.class)
                .returnResult()
                .getResponseBody();

            Assertions.assertNotNull(responseBody);
            assertThat(responseBody.size()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("(POST)Create offer endpoint tests")
    class CreateOfferEndpointTests {

        @Test
        @DisplayName("Should return 200 (OK) with created offer entity")
        void testCreateOffer() {
            Offer responseBody = webTestClient
                .post()
                .uri(OFFER_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new OfferDTO("some-name", "some-description", offerType.getId(), null))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Offer.class)
                .returnResult()
                .getResponseBody();

            Assertions.assertNotNull(responseBody);
            assertThat(responseBody.getName()).isEqualTo("some-name");
            assertThat(responseBody.getDescription()).isEqualTo("some-description");

            offerRepository.delete(responseBody).block();
        }

        @Test
        @DisplayName("Should return 400 (BAD REQUEST) for validation fail on payload")
        void testCreateOfferInvalidPayload() {
            webTestClient
                .post()
                .uri(OFFER_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new OfferDTO(null, "some-description", offerType.getId(), null))
                .exchange()
                .expectStatus()
                .isBadRequest();
        }
    }

    @Nested
    @DisplayName("(PATCH)Partial update offer endpoint tests")
    class PartialUpdateOfferEndpointTests {

        @Test
        @DisplayName("Should return 200 (OK) for successful update of an offer")
        void testPartialUpdateOffer() {
            Offer responseBody = webTestClient
                .patch()
                .uri(OFFER_ID_API_URL, existingOffer.getId())
                .bodyValue(new OfferUpdateDTO("new-name", null, null, null))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Offer.class)
                .returnResult()
                .getResponseBody();

            Assertions.assertNotNull(responseBody);
            assertThat(responseBody.getName()).isEqualTo("new-name");
        }

        @Test
        @DisplayName("Should return 400 (BAD REQUEST) for id type mismatch")
        void testPartialUpdateOfferIdTypeMismatch() {
            webTestClient
                .patch()
                .uri(OFFER_ID_API_URL, "id-type-mismatch")
                .bodyValue(new OfferUpdateDTO("test", null, null, null))
                .exchange()
                .expectStatus()
                .isBadRequest();
        }

        @Test
        @DisplayName("Should return 400 (BAD REQUEST) for no payload provided")
        void testPartialUpdateOfferNoPayloadProvided() {
            webTestClient.patch().uri(OFFER_ID_API_URL, existingOffer.getId()).exchange().expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Should return 404 (NOT FOUND) for non existing offer")
        void testPartialUpdateOfferNotFound() {
            webTestClient
                .patch()
                .uri(OFFER_ID_API_URL, Long.MAX_VALUE)
                .bodyValue(new OfferUpdateDTO("test", null, null, null))
                .exchange()
                .expectStatus()
                .isNotFound();
        }
    }

    @Nested
    @DisplayName("(DELETE)Delete offer endpoint tests")
    class DeleteOfferEndpointTests {

        @Test
        @DisplayName("Should return 200 (OK) and delete an existing offer")
        void testDeleteOffer() {
            Offer offer = offerRepository.save(createOffer(profile, offerType)).block();

            Assertions.assertNotNull(offer);
            webTestClient.delete().uri(OFFER_ID_API_URL, offer.getId()).exchange().expectStatus().isOk();
        }

        @Test
        @DisplayName("Should return 404 (NOT FOUND) for non existing offer")
        void testDeleteOfferNotFound() {
            webTestClient.delete().uri(OFFER_ID_API_URL, Long.MAX_VALUE).exchange().expectStatus().isNotFound();
        }
    }
}
