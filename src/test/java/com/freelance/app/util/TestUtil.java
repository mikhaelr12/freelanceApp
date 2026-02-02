package com.freelance.app.util;

import com.freelance.app.domain.*;
import com.freelance.app.domain.enumeration.OfferStatus;
import com.freelance.app.domain.enumeration.ProfileType;
import com.freelance.app.service.dto.ProfileCreationDTO;
import com.freelance.app.service.dto.ProfileEditDTO;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for testing REST controllers.
 */
@Component
public final class TestUtil {

    private static String applicationName;

    @Value("${jhipster.clientApp.name:app}")
    private String appName;

    @PostConstruct
    public void init() {
        applicationName = appName;
    }

    //API URLs
    //Profile URLs
    public static final String PROFILE_API_URL = "/api/profiles";
    public static final String PROFILE_ID_API_URL = PROFILE_API_URL + "/{id}";

    //Favorite offer URLs
    public static final String FAVORITE_OFFER_API_URL = "/api/favorite-offers";
    public static final String FAVORITE_OFFER_ID_API_URL = FAVORITE_OFFER_API_URL + "/{offerId}";
    public static final String FAVORITE_OFFER_REMOVE_API_URL = FAVORITE_OFFER_API_URL + "/remove/{favoriteOfferId}";

    //Offer URLs
    public static final String OFFER_API_URL = "/api/offers";
    public static final String OFFER_ID_API_URL = OFFER_API_URL + "/{offerId}";

    //Headers
    public static String getApplicationName() {
        return applicationName != null ? applicationName : "app";
    }

    public static String getAlertHeader() {
        return "X-" + getApplicationName() + "-alert";
    }

    public static final String X_TOTAL_COUNT = "X-Total-Count";

    //Entity creation methods
    public static User createUser() {
        User user = new User();
        user.setLogin("testuser");
        user.setEmail("testUser@test.com");
        user.setPassword("testPassword");
        user.setCreatedBy("testUser");
        user.setCreatedDate(Instant.now());
        return user;
    }

    public static ProfileCreationDTO createProfileCreationDTO() {
        return new ProfileCreationDTO("testFirsName", "testLastName", "testDescription", ProfileType.CLIENT, null);
    }

    public static Profile createProfile(User user) {
        return new Profile().firstName("testFirstName").lastName("testLastName").user(user);
    }

    public static ProfileEditDTO createProfileEditDTO() {
        return new ProfileEditDTO("newFirstName", null, null, null);
    }

    public static Offer createOffer(Profile owner, OfferType offerType) {
        return new Offer()
            .name("testName")
            .description("testDescription")
            .status(OfferStatus.ACTIVE)
            .visibility(true)
            .owner(owner)
            .offertype(offerType);
    }

    public static FavoriteOffer createFavoriteOffer(Profile profile, Offer offer) {
        return new FavoriteOffer().profile(profile).offer(offer);
    }

    public static List<Offer> createTempOffers(OfferType offerType, int range, Profile profile) {
        return IntStream.rangeClosed(1, range)
            .mapToObj(i ->
                new Offer()
                    .name("testName " + i)
                    .description("testDescription " + i)
                    .owner(profile)
                    .status(OfferStatus.ACTIVE)
                    .visibility(true)
                    .offertype(offerType)
            )
            .toList();
    }
}
