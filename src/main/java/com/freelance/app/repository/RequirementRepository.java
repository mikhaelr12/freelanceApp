package com.freelance.app.repository;

import com.freelance.app.domain.Requirement;
import com.freelance.app.domain.criteria.RequirementCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Requirement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RequirementRepository extends ReactiveCrudRepository<Requirement, Long>, RequirementRepositoryInternal {
    Flux<Requirement> findAllBy(Pageable pageable);

    @Query("SELECT * FROM requirement entity WHERE entity.order_id = :id")
    Flux<Requirement> findByOrder(Long id);

    @Query("SELECT * FROM requirement entity WHERE entity.order_id IS NULL")
    Flux<Requirement> findAllWhereOrderIsNull();

    @Override
    <S extends Requirement> Mono<S> save(S entity);

    @Override
    Flux<Requirement> findAll();

    @Override
    Mono<Requirement> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RequirementRepositoryInternal {
    <S extends Requirement> Mono<S> save(S entity);

    Flux<Requirement> findAllBy(Pageable pageable);

    Flux<Requirement> findAll();

    Mono<Requirement> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Requirement> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Requirement> findByCriteria(RequirementCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(RequirementCriteria criteria);
}
