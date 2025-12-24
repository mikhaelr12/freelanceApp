package com.freelance.app.repository;

import com.freelance.app.domain.OfferReview;
import com.freelance.app.domain.criteria.OfferReviewCriteria;
import com.freelance.app.service.dto.OfferReviewShortDTO;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT * FROM offer_review entity WHERE entity.offer_id = :offerId AND entity.checked = false")
    Flux<OfferReview> findAllWhereCheckedIsFalse(@Param("offerId") Long offerId);

    @Query("SELECT AVG(entity.rating) FROM offer_review entity WHERE entity.offer_id = :offerId")
    Mono<Double> getAverageRatingOffer(@Param("offerId") Long offerId);

    @Query("UPDATE offer_review SET checked = true WHERE id IN (:reviewIds)")
    Mono<Void> checkReviews(@Param("reviewIds") List<Long> reviewIds);

    @Query(
        """
            SELECT
                r.id AS id,
                r.text AS text,
                r.rating AS rating,
                p.id AS "profile_id",
                concat(p.first_name, ' ', p.last_name) AS "profile_full_name"
            FROM offer_review r
            JOIN public.profile p ON r.reviewer_id = p.id
            WHERE r.offer_id = :offerId
            ORDER BY r.id DESC
            LIMIT :limit OFFSET :offset
        """
    )
    Flux<OfferReviewShortDTO> findByOfferPaged(@Param("offerId") Long offerId, @Param("limit") long limit, @Param("offset") long offset);

    @Query(
        """
        SELECT
                r.id AS id,
                r.text AS text,
                r.rating AS rating,
                p.id AS "profile_id",
                concat(p.first_name, ' ', p.last_name) AS "profile_full_name"
            FROM offer_review r
            JOIN public.profile p ON p.id = :reviewerId
            WHERE r.offer_id = :offerId
        """
    )
    Mono<OfferReviewShortDTO> findMyOfferReview(@Param("offerId") Long offerId, @Param("reviewerId") Long reviewerId);

    @Query(
        """
        SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END
        FROM offer_review r
        WHERE r.reviewer_id = :reviewerId
        """
    )
    Mono<Boolean> existsByReviewerId(@Param("reviewerId") Long reviewerId);
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
