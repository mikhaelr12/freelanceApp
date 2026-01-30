package com.freelance.app.repository;

import com.freelance.app.domain.Order;
import com.freelance.app.domain.criteria.OrderCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long>, OrderRepositoryInternal {
    Flux<Order> findAllBy(Pageable pageable);

    @Override
    <S extends Order> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Order> findAll();

    @Override
    @NotNull
    Mono<Order> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface OrderRepositoryInternal {
    <S extends Order> Mono<S> save(S entity);

    Flux<Order> findAllBy(Pageable pageable);

    Flux<Order> findAll();

    Mono<Order> findById(Long id);

    Flux<Order> findByCriteria(OrderCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(OrderCriteria criteria);

    Mono<Void> deleteById(Long id);
}
