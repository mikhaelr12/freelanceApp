package com.freelance.app.service;

import com.freelance.app.domain.criteria.ProfileCriteria;
import com.freelance.app.repository.ProfileRepository;
import com.freelance.app.service.dto.ProfileDTO;
import com.freelance.app.service.mapper.ProfileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Profile}.
 */
@Service
@Transactional
public class ProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;

    private final ProfileMapper profileMapper;

    public ProfileService(ProfileRepository profileRepository, ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
    }

    /**
     * Save a profile.
     *
     * @param profileDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProfileDTO> save(ProfileDTO profileDTO) {
        LOG.debug("Request to save Profile : {}", profileDTO);
        return profileRepository.save(profileMapper.toEntity(profileDTO)).map(profileMapper::toDto);
    }

    /**
     * Update a profile.
     *
     * @param profileDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProfileDTO> update(ProfileDTO profileDTO) {
        LOG.debug("Request to update Profile : {}", profileDTO);
        return profileRepository.save(profileMapper.toEntity(profileDTO)).map(profileMapper::toDto);
    }

    /**
     * Partially update a profile.
     *
     * @param profileDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProfileDTO> partialUpdate(ProfileDTO profileDTO) {
        LOG.debug("Request to partially update Profile : {}", profileDTO);

        return profileRepository
            .findById(profileDTO.getId())
            .map(existingProfile -> {
                profileMapper.partialUpdate(existingProfile, profileDTO);

                return existingProfile;
            })
            .flatMap(profileRepository::save)
            .map(profileMapper::toDto);
    }

    /**
     * Find profiles by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProfileDTO> findByCriteria(ProfileCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Profiles by Criteria");
        return profileRepository.findByCriteria(criteria, pageable).map(profileMapper::toDto);
    }

    /**
     * Find the count of profiles by criteria.
     * @param criteria filtering criteria
     * @return the count of profiles
     */
    public Mono<Long> countByCriteria(ProfileCriteria criteria) {
        LOG.debug("Request to get the count of all Profiles by Criteria");
        return profileRepository.countByCriteria(criteria);
    }

    /**
     * Get all the profiles with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProfileDTO> findAllWithEagerRelationships(Pageable pageable) {
        return profileRepository.findAllWithEagerRelationships(pageable).map(profileMapper::toDto);
    }

    /**
     * Returns the number of profiles available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return profileRepository.count();
    }

    /**
     * Get one profile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProfileDTO> findOne(Long id) {
        LOG.debug("Request to get Profile : {}", id);
        return profileRepository.findOneWithEagerRelationships(id).map(profileMapper::toDto);
    }

    /**
     * Delete the profile by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Profile : {}", id);
        return profileRepository.deleteById(id);
    }
}
