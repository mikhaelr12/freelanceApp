package com.freelance.app.service;

import com.freelance.app.domain.criteria.FavoriteOfferCriteria;
import com.freelance.app.repository.FavoriteOfferRepository;
import com.freelance.app.service.dto.FavoriteOfferDTO;
import com.freelance.app.service.mapper.FavoriteOfferMapper;
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

    private final FavoriteOfferMapper favoriteOfferMapper;

    public FavoriteOfferService(FavoriteOfferRepository favoriteOfferRepository, FavoriteOfferMapper favoriteOfferMapper) {
        this.favoriteOfferRepository = favoriteOfferRepository;
        this.favoriteOfferMapper = favoriteOfferMapper;
    }

    /**
     * Save a favoriteOffer.
     *
     * @param favoriteOfferDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<FavoriteOfferDTO> save(FavoriteOfferDTO favoriteOfferDTO) {
        LOG.debug("Request to save FavoriteOffer : {}", favoriteOfferDTO);
        return favoriteOfferRepository.save(favoriteOfferMapper.toEntity(favoriteOfferDTO)).map(favoriteOfferMapper::toDto);
    }

    /**
     * Update a favoriteOffer.
     *
     * @param favoriteOfferDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<FavoriteOfferDTO> update(FavoriteOfferDTO favoriteOfferDTO) {
        LOG.debug("Request to update FavoriteOffer : {}", favoriteOfferDTO);
        return favoriteOfferRepository.save(favoriteOfferMapper.toEntity(favoriteOfferDTO)).map(favoriteOfferMapper::toDto);
    }

    /**
     * Partially update a favoriteOffer.
     *
     * @param favoriteOfferDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<FavoriteOfferDTO> partialUpdate(FavoriteOfferDTO favoriteOfferDTO) {
        LOG.debug("Request to partially update FavoriteOffer : {}", favoriteOfferDTO);

        return favoriteOfferRepository
            .findById(favoriteOfferDTO.getId())
            .map(existingFavoriteOffer -> {
                favoriteOfferMapper.partialUpdate(existingFavoriteOffer, favoriteOfferDTO);

                return existingFavoriteOffer;
            })
            .flatMap(favoriteOfferRepository::save)
            .map(favoriteOfferMapper::toDto);
    }

    /**
     * Find favoriteOffers by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FavoriteOfferDTO> findByCriteria(FavoriteOfferCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all FavoriteOffers by Criteria");
        return favoriteOfferRepository.findByCriteria(criteria, pageable).map(favoriteOfferMapper::toDto);
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
    public Mono<FavoriteOfferDTO> findOne(Long id) {
        LOG.debug("Request to get FavoriteOffer : {}", id);
        return favoriteOfferRepository.findById(id).map(favoriteOfferMapper::toDto);
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
