package com.freelance.app.service;

import com.freelance.app.domain.criteria.OrderCriteria;
import com.freelance.app.repository.OrderRepository;
import com.freelance.app.service.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Order}.
 */
@Service
@Transactional
public class OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Save a order.
     *
     * @param orderDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderDTO> save(OrderDTO orderDTO) {
        LOG.debug("Request to save Order : {}", orderDTO);
        return null;
    }

    /**
     * Update a order.
     *
     * @param orderDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderDTO> update(OrderDTO orderDTO) {
        LOG.debug("Request to update Order : {}", orderDTO);
        return null;
    }

    /**
     * Partially update a order.
     *
     * @param orderDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OrderDTO> partialUpdate(OrderDTO orderDTO) {
        LOG.debug("Request to partially update Order : {}", orderDTO);
        return null;
    }

    /**
     * Find orders by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrderDTO> findByCriteria(OrderCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Orders by Criteria");
        return null;
    }

    /**
     * Find the count of orders by criteria.
     * @param criteria filtering criteria
     * @return the count of orders
     */
    public Mono<Long> countByCriteria(OrderCriteria criteria) {
        LOG.debug("Request to get the count of all Orders by Criteria");
        return orderRepository.countByCriteria(criteria);
    }

    /**
     * Get all the orders with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<OrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return null;
    }

    /**
     * Returns the number of orders available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return orderRepository.count();
    }

    /**
     * Get one order by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OrderDTO> findOne(Long id) {
        LOG.debug("Request to get Order : {}", id);
        return null;
    }

    /**
     * Delete the order by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Order : {}", id);
        return orderRepository.deleteById(id);
    }
}
