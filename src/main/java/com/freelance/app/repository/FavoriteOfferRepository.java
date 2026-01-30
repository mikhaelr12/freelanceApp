package com.freelance.app.repository;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.criteria.FavoriteOfferCriteria;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the FavoriteOffer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FavoriteOfferRepository extends ReactiveCrudRepository<FavoriteOffer, Long>, FavoriteOfferRepositoryInternal {
    Flux<FavoriteOffer> findAllBy(Pageable pageable);

    @Override
    <S extends FavoriteOffer> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<FavoriteOffer> findAll();

    @Override
    @NotNull
    Mono<FavoriteOffer> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface FavoriteOfferRepositoryInternal {
    <S extends FavoriteOffer> Mono<S> save(S entity);

    Flux<FavoriteOffer> findAllBy(Pageable pageable);

    Flux<FavoriteOffer> findAll();

    Mono<FavoriteOffer> findById(Long id);

    Flux<FavoriteOffer> findByCriteria(FavoriteOfferCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(FavoriteOfferCriteria criteria);

    Flux<FavoriteOffer> findAllByProfileId(Long profileId);
}
