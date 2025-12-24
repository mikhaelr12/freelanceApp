package com.freelance.app.repository;

import com.freelance.app.domain.OfferMedia;
import com.freelance.app.domain.criteria.OfferMediaCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the OfferMedia entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OfferMediaRepository extends ReactiveCrudRepository<OfferMedia, Long>, OfferMediaRepositoryInternal {
    Flux<OfferMedia> findAllBy(Pageable pageable);

    @Override
    Mono<OfferMedia> findOneWithEagerRelationships(Long id);

    @Override
    Flux<OfferMedia> findAllWithEagerRelationships();

    @Override
    Flux<OfferMedia> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM offer_media entity WHERE entity.offer_id = :id")
    Flux<OfferMedia> findByOffer(Long id);

    @Query("SELECT * FROM offer_media entity WHERE entity.offer_id IS NULL")
    Flux<OfferMedia> findAllWhereOfferIsNull();

    @Query("SELECT * FROM offer_media entity WHERE entity.file_id = :id")
    Flux<OfferMedia> findByFile(Long id);

    @Query("SELECT * FROM offer_media entity WHERE entity.file_id IS NULL")
    Flux<OfferMedia> findAllWhereFileIsNull();

    @Override
    <S extends OfferMedia> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<OfferMedia> findAll();

    @Override
    @NotNull
    Mono<OfferMedia> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);

    @Query("DELETE FROM offer_media entity WHERE entity.offer_id = :offerId")
    Mono<Void> deleteAllByOffer(@Param("offerId") Long offerId);
}

interface OfferMediaRepositoryInternal {
    <S extends OfferMedia> Mono<S> save(S entity);

    Flux<OfferMedia> findAllBy(Pageable pageable);

    Flux<OfferMedia> findAll();

    Mono<OfferMedia> findById(Long id);

    Flux<OfferMedia> findByCriteria(OfferMediaCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(OfferMediaCriteria criteria);

    Mono<OfferMedia> findOneWithEagerRelationships(Long id);

    Flux<OfferMedia> findAllWithEagerRelationships();

    Flux<OfferMedia> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
