package com.freelance.app.repository;

import com.freelance.app.domain.Delivery;
import com.freelance.app.domain.criteria.DeliveryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
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

    @Query("SELECT * FROM delivery entity WHERE entity.order_id = :id")
    Flux<Delivery> findByOrder(Long id);

    @Query("SELECT * FROM delivery entity WHERE entity.order_id IS NULL")
    Flux<Delivery> findAllWhereOrderIsNull();

    @Query("SELECT * FROM delivery entity WHERE entity.file_id = :id")
    Flux<Delivery> findByFile(Long id);

    @Query("SELECT * FROM delivery entity WHERE entity.file_id IS NULL")
    Flux<Delivery> findAllWhereFileIsNull();

    @Override
    <S extends Delivery> Mono<S> save(S entity);

    @Override
    Flux<Delivery> findAll();

    @Override
    Mono<Delivery> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DeliveryRepositoryInternal {
    <S extends Delivery> Mono<S> save(S entity);

    Flux<Delivery> findAllBy(Pageable pageable);

    Flux<Delivery> findAll();

    Mono<Delivery> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Delivery> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Delivery> findByCriteria(DeliveryCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(DeliveryCriteria criteria);

    Mono<Delivery> findOneWithEagerRelationships(Long id);

    Flux<Delivery> findAllWithEagerRelationships();

    Flux<Delivery> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
