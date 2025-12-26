package com.freelance.app.service;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.criteria.FavoriteOfferCriteria;
import com.freelance.app.repository.FavoriteOfferRepository;
import com.freelance.app.repository.OfferRepository;
import com.freelance.app.util.ProfileHelper;
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
    private final ProfileHelper profileHelper;
    private final OfferRepository offerRepository;

    public FavoriteOfferService(
        FavoriteOfferRepository favoriteOfferRepository,
        ProfileHelper profileHelper,
        OfferRepository offerRepository
    ) {
        this.favoriteOfferRepository = favoriteOfferRepository;
        this.profileHelper = profileHelper;
        this.offerRepository = offerRepository;
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

    /**
     * Create new favorite offer.
     *
     * @param offerId the id of offer to be added to favorites.
     * @return the entity created.
     */
    public Mono<FavoriteOffer> createFavoriteOffer(Long offerId) {
        return profileHelper
            .getCurrentProfile()
            .zipWith(offerRepository.findById(offerId))
            .flatMap(tuple -> favoriteOfferRepository.save(new FavoriteOffer().offer(tuple.getT2()).profile(tuple.getT1())));
    }
}
