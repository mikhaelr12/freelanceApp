package com.freelance.app.repository;

import com.freelance.app.domain.OfferReview;
import com.freelance.app.domain.criteria.OfferReviewCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the OfferReview entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OfferReviewRepository extends ReactiveCrudRepository<OfferReview, Long>, OfferReviewRepositoryInternal {
    Flux<OfferReview> findAllBy(Pageable pageable);

    @Override
    Mono<OfferReview> findOneWithEagerRelationships(Long id);

    @Override
    Flux<OfferReview> findAllWithEagerRelationships();

    @Override
    Flux<OfferReview> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM offer_review entity WHERE entity.offer_id = :id")
    Flux<OfferReview> findByOffer(Long id);

    @Query("SELECT * FROM offer_review entity WHERE entity.offer_id IS NULL")
    Flux<OfferReview> findAllWhereOfferIsNull();

    @Query("SELECT * FROM offer_review entity WHERE entity.reviewer_id = :id")
    Flux<OfferReview> findByReviewer(Long id);

    @Query("SELECT * FROM offer_review entity WHERE entity.reviewer_id IS NULL")
    Flux<OfferReview> findAllWhereReviewerIsNull();

    @Override
    <S extends OfferReview> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<OfferReview> findAll();

    @Override
    @NotNull
    Mono<OfferReview> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface OfferReviewRepositoryInternal {
    <S extends OfferReview> Mono<S> save(S entity);

    Flux<OfferReview> findAllBy(Pageable pageable);

    Flux<OfferReview> findAll();

    Mono<OfferReview> findById(Long id);

    Flux<OfferReview> findByCriteria(OfferReviewCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(OfferReviewCriteria criteria);

    Mono<OfferReview> findOneWithEagerRelationships(Long id);

    Flux<OfferReview> findAllWithEagerRelationships();

    Flux<OfferReview> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
