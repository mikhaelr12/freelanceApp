package com.freelance.app.web.rest;

import com.freelance.app.domain.Profile;
import com.freelance.app.service.ProfileService;
import com.freelance.app.service.dto.ProfileCreationDTO;
import com.freelance.app.service.dto.ProfileDTO;
import com.freelance.app.service.dto.ProfileEditDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.freelance.app.domain.Profile}.
 */
@RestController
@RequestMapping("/api/profiles")
public class ProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileResource.class);

    private static final String ENTITY_NAME = "profile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProfileService profileService;

    public ProfileResource(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * {@code POST  /profiles} : Create a new profile.
     *
     * @param profileDTO the profileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new profileDTO,
     * or with status {@code 400 (Bad Request)} if the profile has already an ID.
     */
    @PostMapping(value = "")
    public Mono<ResponseEntity<Profile>> createProfile(@Valid @RequestBody ProfileCreationDTO profileDTO) {
        LOG.debug("REST request to save Profile : {}", profileDTO);
        return profileService
            .createProfile(profileDTO)
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile could not be created")));
    }

    /**
     * {@code PUT  /profiles/:id} : Updates an existing profile.
     *
     * @param id         the id of the profileDTO to save.
     * @param profileDTO the profileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileDTO,
     * or with status {@code 400 (Bad Request)} if the profileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the profileDTO couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Profile>> updateProfile(@PathVariable Long id, @Valid @RequestBody ProfileEditDTO profileDTO) {
        LOG.debug("REST request to update Profile : {}, {}", id, profileDTO);
        return profileService.update(profileDTO, id).map(ResponseEntity::ok);
    }

    /**
     * {@code GET  /profiles/:id} : get the "id" profile.
     *
     * @param id the id of the profileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProfileDTO>> getProfile(@PathVariable Long id) {
        LOG.debug("REST request to get Profile : {}", id);
        Mono<ProfileDTO> profileDTO = profileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(profileDTO);
    }

    /**
     * {@code DELETE  /profiles/:id} : delete the "id" profile.
     *
     * @param id the id of the profileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProfile(@PathVariable Long id) {
        LOG.debug("REST request to delete Profile : {}", id);
        return profileService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    @PatchMapping(value = "/upload/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Void>> uploadProfilePicture(@RequestPart("profile-picture") FilePart profilePicture) {
        return profileService.uploadProfilePicture(profilePicture).then(Mono.just(ResponseEntity.ok().build()));
    }
}
