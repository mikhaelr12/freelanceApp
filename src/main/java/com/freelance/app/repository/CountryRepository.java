package com.freelance.app.repository;

import com.freelance.app.domain.Country;
import com.freelance.app.domain.criteria.CountryCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Country entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CountryRepository extends ReactiveCrudRepository<Country, Long>, CountryRepositoryInternal {
    Flux<Country> findAllBy(Pageable pageable);

    @Override
    <S extends Country> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Country> findAll();

    @Override
    @NotNull
    Mono<Country> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface CountryRepositoryInternal {
    <S extends Country> Mono<S> save(S entity);

    Flux<Country> findAllBy(Pageable pageable);

    Flux<Country> findAll();

    Mono<Country> findById(Long id);

    Flux<Country> findByCriteria(CountryCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(CountryCriteria criteria);
}
