package com.freelance.app.repository;

import com.freelance.app.domain.ProfileReview;
import com.freelance.app.domain.criteria.ProfileReviewCriteria;
import org.springframework.data.domain.Pageable;
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

    @Override
    <S extends ProfileReview> Mono<S> save(S entity);

    @Override
    Flux<ProfileReview> findAll();

    @Override
    Mono<ProfileReview> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProfileReviewRepositoryInternal {
    <S extends ProfileReview> Mono<S> save(S entity);

    Flux<ProfileReview> findAllBy(Pageable pageable);

    Flux<ProfileReview> findAll();

    Mono<ProfileReview> findById(Long id);

    Flux<ProfileReview> findByCriteria(ProfileReviewCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ProfileReviewCriteria criteria);
}
