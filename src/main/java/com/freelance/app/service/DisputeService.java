package com.freelance.app.service;

import com.freelance.app.domain.criteria.DisputeCriteria;
import com.freelance.app.repository.DisputeRepository;
import com.freelance.app.service.dto.DisputeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Dispute}.
 */
@Service
@Transactional
public class DisputeService {

    private static final Logger LOG = LoggerFactory.getLogger(DisputeService.class);

    private final DisputeRepository disputeRepository;

    public DisputeService(DisputeRepository disputeRepository) {
        this.disputeRepository = disputeRepository;
    }

    /**
     * Save a dispute.
     *
     * @param disputeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DisputeDTO> save(DisputeDTO disputeDTO) {
        LOG.debug("Request to save Dispute : {}", disputeDTO);
        return null;
    }

    /**
     * Update a dispute.
     *
     * @param disputeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DisputeDTO> update(DisputeDTO disputeDTO) {
        LOG.debug("Request to update Dispute : {}", disputeDTO);
        return null;
    }

    /**
     * Partially update a dispute.
     *
     * @param disputeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<DisputeDTO> partialUpdate(DisputeDTO disputeDTO) {
        LOG.debug("Request to partially update Dispute : {}", disputeDTO);

        return null;
    }

    /**
     * Find disputes by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DisputeDTO> findByCriteria(DisputeCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Disputes by Criteria");
        return null;
    }

    /**
     * Find the count of disputes by criteria.
     * @param criteria filtering criteria
     * @return the count of disputes
     */
    public Mono<Long> countByCriteria(DisputeCriteria criteria) {
        LOG.debug("Request to get the count of all Disputes by Criteria");
        return disputeRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of disputes available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return disputeRepository.count();
    }

    /**
     * Get one dispute by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<DisputeDTO> findOne(Long id) {
        LOG.debug("Request to get Dispute : {}", id);
        return null;
    }

    /**
     * Delete the dispute by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Dispute : {}", id);
        return disputeRepository.deleteById(id);
    }
}
