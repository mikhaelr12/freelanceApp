package com.freelance.app.repository;

import com.freelance.app.domain.Requirement;
import com.freelance.app.domain.criteria.RequirementCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
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

    @Override
    <S extends Requirement> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Requirement> findAll();

    @Override
    @NotNull
    Mono<Requirement> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface RequirementRepositoryInternal {
    <S extends Requirement> Mono<S> save(S entity);

    Flux<Requirement> findAllBy(Pageable pageable);

    Flux<Requirement> findAll();

    Mono<Requirement> findById(Long id);

    Flux<Requirement> findByCriteria(RequirementCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(RequirementCriteria criteria);
}
