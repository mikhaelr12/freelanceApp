package com.freelance.app.repository;

import com.freelance.app.domain.Offer;
import com.freelance.app.domain.criteria.OfferCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
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
    Mono<Offer> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Offer> findAllWithEagerRelationships();

    @Override
    Flux<Offer> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM offer entity WHERE entity.owner_id = :id")
    Flux<Offer> findByOwner(Long id);

    @Query("SELECT * FROM offer entity WHERE entity.owner_id IS NULL")
    Flux<Offer> findAllWhereOwnerIsNull();

    @Query("SELECT * FROM offer entity WHERE entity.offertype_id = :id")
    Flux<Offer> findByOffertype(Long id);

    @Query("SELECT * FROM offer entity WHERE entity.offertype_id IS NULL")
    Flux<Offer> findAllWhereOffertypeIsNull();

    @Query("SELECT entity.* FROM offer entity JOIN rel_offer__tag joinTable ON entity.id = joinTable.tag_id WHERE joinTable.tag_id = :id")
    Flux<Offer> findByTag(Long id);

    @Override
    <S extends Offer> Mono<S> save(S entity);

    @Override
    Flux<Offer> findAll();

    @Override
    Mono<Offer> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface OfferRepositoryInternal {
    <S extends Offer> Mono<S> save(S entity);

    Flux<Offer> findAllBy(Pageable pageable);

    Flux<Offer> findAll();

    Mono<Offer> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Offer> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Offer> findByCriteria(OfferCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(OfferCriteria criteria);

    Mono<Offer> findOneWithEagerRelationships(Long id);

    Flux<Offer> findAllWithEagerRelationships();

    Flux<Offer> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
