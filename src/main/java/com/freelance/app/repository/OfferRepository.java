package com.freelance.app.repository;

import com.freelance.app.domain.Offer;
import com.freelance.app.domain.criteria.OfferCriteria;
import com.freelance.app.service.dto.OfferShortDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Offer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OfferRepository extends ReactiveCrudRepository<Offer, Long>, OfferRepositoryInternal {
    Flux<Offer> findAllBy(Pageable pageable);

    @Override
    <S extends Offer> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Offer> findAll();

    @Override
    @NotNull
    Mono<Offer> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface OfferRepositoryInternal {
    <S extends Offer> Mono<S> save(S entity);

    Flux<Offer> findAllBy(Pageable pageable);

    Flux<Offer> findAll();

    Mono<Offer> findById(Long id);

    Flux<Offer> findByCriteria(OfferCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(OfferCriteria criteria);

    Mono<Void> deleteById(Long id);

    Flux<OfferShortDTO> findByCriteriaShort(OfferCriteria criteria, Pageable pageable);
}
