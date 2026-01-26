package com.freelance.app.util;

import com.freelance.app.domain.Profile;
import com.freelance.app.domain.User;
import com.freelance.app.domain.enumeration.ProfileType;
import com.freelance.app.service.dto.ProfileCreationDTO;
import com.freelance.app.service.dto.ProfileEditDTO;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
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

    public static String getApplicationName() {
        return applicationName != null ? applicationName : "app";
    }

    public static String getAlertHeader() {
        return "X-" + getApplicationName() + "-alert";
    }

    //API URLs
    public static final String PROFILE_API_URL = "/api/profiles";
    public static final String PROFILE_ID_API_URL = PROFILE_API_URL + "/{id}";

    public static User createUser() {
        User user = new User();
        user.setLogin("testUser");
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
}
