package com.freelance.app.service;

import com.freelance.app.domain.criteria.ProfileReviewCriteria;
import com.freelance.app.repository.ProfileReviewRepository;
import com.freelance.app.service.dto.ProfileReviewDTO;
import com.freelance.app.service.mapper.ProfileReviewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.ProfileReview}.
 */
@Service
@Transactional
public class ProfileReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileReviewService.class);

    private final ProfileReviewRepository profileReviewRepository;

    private final ProfileReviewMapper profileReviewMapper;

    public ProfileReviewService(ProfileReviewRepository profileReviewRepository, ProfileReviewMapper profileReviewMapper) {
        this.profileReviewRepository = profileReviewRepository;
        this.profileReviewMapper = profileReviewMapper;
    }

    /**
     * Save a profileReview.
     *
     * @param profileReviewDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProfileReviewDTO> save(ProfileReviewDTO profileReviewDTO) {
        LOG.debug("Request to save ProfileReview : {}", profileReviewDTO);
        return profileReviewRepository.save(profileReviewMapper.toEntity(profileReviewDTO)).map(profileReviewMapper::toDto);
    }

    /**
     * Update a profileReview.
     *
     * @param profileReviewDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProfileReviewDTO> update(ProfileReviewDTO profileReviewDTO) {
        LOG.debug("Request to update ProfileReview : {}", profileReviewDTO);
        return profileReviewRepository.save(profileReviewMapper.toEntity(profileReviewDTO)).map(profileReviewMapper::toDto);
    }

    /**
     * Partially update a profileReview.
     *
     * @param profileReviewDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProfileReviewDTO> partialUpdate(ProfileReviewDTO profileReviewDTO) {
        LOG.debug("Request to partially update ProfileReview : {}", profileReviewDTO);

        return profileReviewRepository
            .findById(profileReviewDTO.getId())
            .map(existingProfileReview -> {
                profileReviewMapper.partialUpdate(existingProfileReview, profileReviewDTO);

                return existingProfileReview;
            })
            .flatMap(profileReviewRepository::save)
            .map(profileReviewMapper::toDto);
    }

    /**
     * Find profileReviews by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProfileReviewDTO> findByCriteria(ProfileReviewCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all ProfileReviews by Criteria");
        return profileReviewRepository.findByCriteria(criteria, pageable).map(profileReviewMapper::toDto);
    }

    /**
     * Find the count of profileReviews by criteria.
     * @param criteria filtering criteria
     * @return the count of profileReviews
     */
    public Mono<Long> countByCriteria(ProfileReviewCriteria criteria) {
        LOG.debug("Request to get the count of all ProfileReviews by Criteria");
        return profileReviewRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of profileReviews available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return profileReviewRepository.count();
    }

    /**
     * Get one profileReview by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProfileReviewDTO> findOne(Long id) {
        LOG.debug("Request to get ProfileReview : {}", id);
        return profileReviewRepository.findById(id).map(profileReviewMapper::toDto);
    }

    /**
     * Delete the profileReview by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ProfileReview : {}", id);
        return profileReviewRepository.deleteById(id);
    }
}
