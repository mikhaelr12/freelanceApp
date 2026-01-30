package com.freelance.app.repository;

import com.freelance.app.domain.Delivery;
import com.freelance.app.domain.criteria.DeliveryCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Delivery entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeliveryRepository extends ReactiveCrudRepository<Delivery, Long>, DeliveryRepositoryInternal {
    Flux<Delivery> findAllBy(Pageable pageable);

    @Override
    Mono<Delivery> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Delivery> findAllWithEagerRelationships();

    @Override
    Flux<Delivery> findAllWithEagerRelationships(Pageable page);

    @Override
    <S extends Delivery> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Delivery> findAll();

    @Override
    @NotNull
    Mono<Delivery> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface DeliveryRepositoryInternal {
    <S extends Delivery> Mono<S> save(S entity);

    Flux<Delivery> findAllBy(Pageable pageable);

    Flux<Delivery> findAll();

    Mono<Delivery> findById(Long id);

    Flux<Delivery> findByCriteria(DeliveryCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(DeliveryCriteria criteria);

    Mono<Delivery> findOneWithEagerRelationships(Long id);

    Flux<Delivery> findAllWithEagerRelationships();

    Flux<Delivery> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
