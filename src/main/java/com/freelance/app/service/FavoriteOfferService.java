package com.freelance.app.service;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.criteria.FavoriteOfferCriteria;
import com.freelance.app.repository.FavoriteOfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.FavoriteOffer}.
 */
@Service
@Transactional
public class FavoriteOfferService {

    private static final Logger LOG = LoggerFactory.getLogger(FavoriteOfferService.class);

    private final FavoriteOfferRepository favoriteOfferRepository;

    public FavoriteOfferService(FavoriteOfferRepository favoriteOfferRepository) {
        this.favoriteOfferRepository = favoriteOfferRepository;
    }

    /**
     * Save a favoriteOffer.
     *
     * @param favoriteOffer the entity to save.
     * @return the persisted entity.
     */
    public Mono<FavoriteOffer> save(FavoriteOffer favoriteOffer) {
        LOG.debug("Request to save FavoriteOffer : {}", favoriteOffer);
        return favoriteOfferRepository.save(favoriteOffer);
    }

    /**
     * Update a favoriteOffer.
     *
     * @param favoriteOffer the entity to save.
     * @return the persisted entity.
     */
    public Mono<FavoriteOffer> update(FavoriteOffer favoriteOffer) {
        LOG.debug("Request to update FavoriteOffer : {}", favoriteOffer);
        return favoriteOfferRepository.save(favoriteOffer);
    }

    /**
     * Partially update a favoriteOffer.
     *
     * @param favoriteOffer the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<FavoriteOffer> partialUpdate(FavoriteOffer favoriteOffer) {
        LOG.debug("Request to partially update FavoriteOffer : {}", favoriteOffer);

        return favoriteOfferRepository
            .findById(favoriteOffer.getId())
            .map(existingFavoriteOffer -> {
                if (favoriteOffer.getCreatedAt() != null) {
                    existingFavoriteOffer.setCreatedAt(favoriteOffer.getCreatedAt());
                }

                return existingFavoriteOffer;
            })
            .flatMap(favoriteOfferRepository::save);
    }

    /**
     * Find favoriteOffers by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FavoriteOffer> findByCriteria(FavoriteOfferCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all FavoriteOffers by Criteria");
        return favoriteOfferRepository.findByCriteria(criteria, pageable);
    }

    /**
     * Find the count of favoriteOffers by criteria.
     * @param criteria filtering criteria
     * @return the count of favoriteOffers
     */
    public Mono<Long> countByCriteria(FavoriteOfferCriteria criteria) {
        LOG.debug("Request to get the count of all FavoriteOffers by Criteria");
        return favoriteOfferRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of favoriteOffers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return favoriteOfferRepository.count();
    }

    /**
     * Get one favoriteOffer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<FavoriteOffer> findOne(Long id) {
        LOG.debug("Request to get FavoriteOffer : {}", id);
        return favoriteOfferRepository.findById(id);
    }

    /**
     * Delete the favoriteOffer by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete FavoriteOffer : {}", id);
        return favoriteOfferRepository.deleteById(id);
    }
}
