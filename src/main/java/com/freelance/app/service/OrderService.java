package com.freelance.app.service;

import com.freelance.app.domain.Order;
import com.freelance.app.domain.criteria.OrderCriteria;
import com.freelance.app.repository.OrderRepository;
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
     * @param order the entity to save.
     * @return the persisted entity.
     */
    public Mono<Order> save(Order order) {
        LOG.debug("Request to save Order : {}", order);
        return orderRepository.save(order);
    }

    /**
     * Update a order.
     *
     * @param order the entity to save.
     * @return the persisted entity.
     */
    public Mono<Order> update(Order order) {
        LOG.debug("Request to update Order : {}", order);
        return orderRepository.save(order);
    }

    /**
     * Partially update a order.
     *
     * @param order the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Order> partialUpdate(Order order) {
        LOG.debug("Request to partially update Order : {}", order);

        return orderRepository
            .findById(order.getId())
            .map(existingOrder -> {
                if (order.getStatus() != null) {
                    existingOrder.setStatus(order.getStatus());
                }
                if (order.getTotalAmount() != null) {
                    existingOrder.setTotalAmount(order.getTotalAmount());
                }
                if (order.getCurrency() != null) {
                    existingOrder.setCurrency(order.getCurrency());
                }
                if (order.getCreatedDate() != null) {
                    existingOrder.setCreatedDate(order.getCreatedDate());
                }
                if (order.getLastModifiedDate() != null) {
                    existingOrder.setLastModifiedDate(order.getLastModifiedDate());
                }
                if (order.getCreatedBy() != null) {
                    existingOrder.setCreatedBy(order.getCreatedBy());
                }
                if (order.getLastModifiedBy() != null) {
                    existingOrder.setLastModifiedBy(order.getLastModifiedBy());
                }

                return existingOrder;
            })
            .flatMap(orderRepository::save);
    }

    /**
     * Find orders by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Order> findByCriteria(OrderCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Orders by Criteria");
        return orderRepository.findByCriteria(criteria, pageable);
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
    public Flux<Order> findAllWithEagerRelationships(Pageable pageable) {
        return orderRepository.findAllWithEagerRelationships(pageable);
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
    public Mono<Order> findOne(Long id) {
        LOG.debug("Request to get Order : {}", id);
        return orderRepository.findOneWithEagerRelationships(id);
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
