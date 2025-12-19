package com.freelance.app.service;

import com.freelance.app.domain.Delivery;
import com.freelance.app.domain.criteria.DeliveryCriteria;
import com.freelance.app.repository.DeliveryRepository;
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
     * @param delivery the entity to save.
     * @return the persisted entity.
     */
    public Mono<Delivery> save(Delivery delivery) {
        LOG.debug("Request to save Delivery : {}", delivery);
        return deliveryRepository.save(delivery);
    }

    /**
     * Update a delivery.
     *
     * @param delivery the entity to save.
     * @return the persisted entity.
     */
    public Mono<Delivery> update(Delivery delivery) {
        LOG.debug("Request to update Delivery : {}", delivery);
        return deliveryRepository.save(delivery);
    }

    /**
     * Partially update a delivery.
     *
     * @param delivery the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Delivery> partialUpdate(Delivery delivery) {
        LOG.debug("Request to partially update Delivery : {}", delivery);

        return deliveryRepository
            .findById(delivery.getId())
            .map(existingDelivery -> {
                if (delivery.getNote() != null) {
                    existingDelivery.setNote(delivery.getNote());
                }
                if (delivery.getDeliveredAt() != null) {
                    existingDelivery.setDeliveredAt(delivery.getDeliveredAt());
                }

                return existingDelivery;
            })
            .flatMap(deliveryRepository::save);
    }

    /**
     * Find deliveries by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Delivery> findByCriteria(DeliveryCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Deliveries by Criteria");
        return deliveryRepository.findByCriteria(criteria, pageable);
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
    public Flux<Delivery> findAllWithEagerRelationships(Pageable pageable) {
        return deliveryRepository.findAllWithEagerRelationships(pageable);
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
    public Mono<Delivery> findOne(Long id) {
        LOG.debug("Request to get Delivery : {}", id);
        return deliveryRepository.findOneWithEagerRelationships(id);
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
