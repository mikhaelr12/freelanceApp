package com.freelance.app.repository;

import com.freelance.app.domain.OfferPackage;
import com.freelance.app.domain.criteria.OfferPackageCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the OfferPackage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OfferPackageRepository extends ReactiveCrudRepository<OfferPackage, Long>, OfferPackageRepositoryInternal {
    Flux<OfferPackage> findAllBy(Pageable pageable);

    @Override
    Mono<OfferPackage> findOneWithEagerRelationships(Long id);

    @Override
    Flux<OfferPackage> findAllWithEagerRelationships();

    @Override
    Flux<OfferPackage> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM offer_package entity WHERE entity.offer_id = :id")
    Flux<OfferPackage> findByOffer(Long id);

    @Query("SELECT * FROM offer_package entity WHERE entity.offer_id IS NULL")
    Flux<OfferPackage> findAllWhereOfferIsNull();

    @Override
    <S extends OfferPackage> Mono<S> save(S entity);

    @Override
    Flux<OfferPackage> findAll();

    @Override
    Mono<OfferPackage> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface OfferPackageRepositoryInternal {
    <S extends OfferPackage> Mono<S> save(S entity);

    Flux<OfferPackage> findAllBy(Pageable pageable);

    Flux<OfferPackage> findAll();

    Mono<OfferPackage> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<OfferPackage> findAllBy(Pageable pageable, Criteria criteria);
    Flux<OfferPackage> findByCriteria(OfferPackageCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(OfferPackageCriteria criteria);

    Mono<OfferPackage> findOneWithEagerRelationships(Long id);

    Flux<OfferPackage> findAllWithEagerRelationships();

    Flux<OfferPackage> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
