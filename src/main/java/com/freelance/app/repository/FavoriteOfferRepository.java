package com.freelance.app.repository;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.criteria.FavoriteOfferCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
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

    @Query("SELECT * FROM favorite_offer entity WHERE entity.profile_id = :id")
    Flux<FavoriteOffer> findByProfile(Long id);

    @Query("SELECT * FROM favorite_offer entity WHERE entity.profile_id IS NULL")
    Flux<FavoriteOffer> findAllWhereProfileIsNull();

    @Query("SELECT * FROM favorite_offer entity WHERE entity.offer_id = :id")
    Flux<FavoriteOffer> findByOffer(Long id);

    @Query("SELECT * FROM favorite_offer entity WHERE entity.offer_id IS NULL")
    Flux<FavoriteOffer> findAllWhereOfferIsNull();

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
}
