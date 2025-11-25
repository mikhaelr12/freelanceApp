package com.freelance.app.repository;

import com.freelance.app.domain.Offer;
import com.freelance.app.domain.criteria.OfferCriteria;
import com.freelance.app.service.dto.OfferShortDTO;
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

    Flux<OfferShortDTO> findByCriteriaShort(OfferCriteria criteria, Pageable pageable);
}
