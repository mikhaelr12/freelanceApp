package com.freelance.app.repository;

import com.freelance.app.domain.Dispute;
import com.freelance.app.domain.criteria.DisputeCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
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

    @Override
    <S extends Dispute> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Dispute> findAll();

    @Override
    @NotNull
    Mono<Dispute> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface DisputeRepositoryInternal {
    <S extends Dispute> Mono<S> save(S entity);

    Flux<Dispute> findAllBy(Pageable pageable);

    Flux<Dispute> findAll();

    Mono<Dispute> findById(Long id);

    Flux<Dispute> findByCriteria(DisputeCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(DisputeCriteria criteria);
}
