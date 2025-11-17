package com.freelance.app.util;

import com.freelance.app.domain.Profile;
import com.freelance.app.repository.ProfileRepository;
import com.freelance.app.repository.UserRepository;
import com.freelance.app.security.SecurityUtils;
import com.freelance.app.web.rest.errors.BadRequestAlertException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ProfileHelper {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public ProfileHelper(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    public Mono<Profile> getCurrentProfile() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .switchIfEmpty(Mono.error(new RuntimeException("Login not found")))
            .flatMap(user ->
                profileRepository
                    .findByUserId(user.getId())
                    .switchIfEmpty(
                        Mono.error(new BadRequestAlertException("Profile not found", user.getId().toString(), "profileNotFound"))
                    )
                    .flatMap(profile -> {
                        profile.setUser(user);
                        return Mono.just(profile);
                    })
            );
    }
}
