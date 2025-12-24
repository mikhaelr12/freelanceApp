package com.freelance.app.repository;

import com.freelance.app.domain.ProfileReview;
import com.freelance.app.domain.criteria.ProfileReviewCriteria;
import com.freelance.app.service.dto.ReviewShortDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProfileReview entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileReviewRepository extends ReactiveCrudRepository<ProfileReview, Long>, ProfileReviewRepositoryInternal {
    Flux<ProfileReview> findAllBy(Pageable pageable);

    @Query("SELECT * FROM profile_review entity WHERE entity.reviewer_id = :id")
    Flux<ProfileReview> findByReviewer(Long id);

    @Query("SELECT * FROM profile_review entity WHERE entity.reviewer_id IS NULL")
    Flux<ProfileReview> findAllWhereReviewerIsNull();

    @Query("SELECT * FROM profile_review entity WHERE entity.reviewee_id = :id")
    Flux<ProfileReview> findByReviewee(Long id);

    @Query("SELECT * FROM profile_review entity WHERE entity.reviewee_id IS NULL")
    Flux<ProfileReview> findAllWhereRevieweeIsNull();

    @Override
    <S extends ProfileReview> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<ProfileReview> findAll();

    @Override
    @NotNull
    Mono<ProfileReview> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);

    @Query(
        """
            SELECT
                r.id AS id,
                r.text AS text,
                r.rating AS rating,
                r.created_date,
                p.id AS "profile_id",
                concat(p.first_name, ' ', p.last_name) AS "profile_full_name"
            FROM profile_review r
            JOIN public.profile p ON :reviewerId = p.id
            ORDER BY r.id DESC
            LIMIT :limit OFFSET :offset
        """
    )
    Flux<ReviewShortDTO> findByOfferPaged(@Param("reviewerId") Long reviewerId, @Param("limit") long limit, @Param("offset") long offset);

    @Query(
        """
        SELECT
                r.id AS id,
                r.text AS text,
                r.rating AS rating,
                p.id AS "profile_id",
                concat(p.first_name, ' ', p.last_name) AS "profile_full_name"
            FROM profile_review r
            JOIN public.profile p ON p.id = :reviewerId
            WHERE r.reviewee_id = :revieweeId
        """
    )
    Mono<ReviewShortDTO> findMyOfferReview(@Param("revieweeId") Long revieweeId, @Param("reviewerId") Long reviewerId);
}

interface ProfileReviewRepositoryInternal {
    <S extends ProfileReview> Mono<S> save(S entity);

    Flux<ProfileReview> findAllBy(Pageable pageable);

    Flux<ProfileReview> findAll();

    Mono<ProfileReview> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProfileReview> findAllBy(Pageable pageable, Criteria criteria);
    Flux<ProfileReview> findByCriteria(ProfileReviewCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ProfileReviewCriteria criteria);
}
