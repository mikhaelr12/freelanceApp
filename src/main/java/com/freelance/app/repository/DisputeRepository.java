package com.freelance.app.repository;

import com.freelance.app.domain.Dispute;
import com.freelance.app.domain.criteria.DisputeCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Dispute entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DisputeRepository extends ReactiveCrudRepository<Dispute, Long>, DisputeRepositoryInternal {
    Flux<Dispute> findAllBy(Pageable pageable);

    @Query("SELECT * FROM dispute entity WHERE entity.order_id = :id")
    Flux<Dispute> findByOrder(Long id);

    @Query("SELECT * FROM dispute entity WHERE entity.order_id IS NULL")
    Flux<Dispute> findAllWhereOrderIsNull();

    @Override
    <S extends Dispute> Mono<S> save(S entity);

    @Override
    Flux<Dispute> findAll();

    @Override
    Mono<Dispute> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DisputeRepositoryInternal {
    <S extends Dispute> Mono<S> save(S entity);

    Flux<Dispute> findAllBy(Pageable pageable);

    Flux<Dispute> findAll();

    Mono<Dispute> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Dispute> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Dispute> findByCriteria(DisputeCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(DisputeCriteria criteria);
}
