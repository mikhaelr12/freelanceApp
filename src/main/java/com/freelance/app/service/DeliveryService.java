package com.freelance.app.service;

import com.freelance.app.domain.criteria.DeliveryCriteria;
import com.freelance.app.repository.DeliveryRepository;
import com.freelance.app.service.dto.DeliveryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Delivery}.
 */
@Service
@Transactional
public class DeliveryService {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Save a delivery.
     *
     * @param deliveryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DeliveryDTO> save(DeliveryDTO deliveryDTO) {
        LOG.debug("Request to save Delivery : {}", deliveryDTO);
        return null;
    }

    /**
     * Update a delivery.
     *
     * @param deliveryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DeliveryDTO> update(DeliveryDTO deliveryDTO) {
        LOG.debug("Request to update Delivery : {}", deliveryDTO);
        return null;
    }

    /**
     * Partially update a delivery.
     *
     * @param deliveryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<DeliveryDTO> partialUpdate(DeliveryDTO deliveryDTO) {
        LOG.debug("Request to partially update Delivery : {}", deliveryDTO);

        return null;
    }

    /**
     * Find deliveries by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DeliveryDTO> findByCriteria(DeliveryCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Deliveries by Criteria");
        return null;
    }

    /**
     * Find the count of deliveries by criteria.
     * @param criteria filtering criteria
     * @return the count of deliveries
     */
    public Mono<Long> countByCriteria(DeliveryCriteria criteria) {
        LOG.debug("Request to get the count of all Deliveries by Criteria");
        return deliveryRepository.countByCriteria(criteria);
    }

    /**
     * Get all the deliveries with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<DeliveryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return null;
    }

    /**
     * Returns the number of deliveries available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return deliveryRepository.count();
    }

    /**
     * Get one delivery by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<DeliveryDTO> findOne(Long id) {
        LOG.debug("Request to get Delivery : {}", id);
        return null;
    }

    /**
     * Delete the delivery by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Delivery : {}", id);
        return deliveryRepository.deleteById(id);
    }
}
