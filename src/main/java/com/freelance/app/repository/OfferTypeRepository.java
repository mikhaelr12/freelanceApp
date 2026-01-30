package com.freelance.app.repository;

import com.freelance.app.domain.OfferType;
import com.freelance.app.domain.criteria.OfferTypeCriteria;
import com.freelance.app.service.dto.OfferTypeShortDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the OfferType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OfferTypeRepository extends ReactiveCrudRepository<OfferType, Long>, OfferTypeRepositoryInternal {
    Flux<OfferType> findAllBy(Pageable pageable);

    @Override
    <S extends OfferType> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<OfferType> findAll();

    @Override
    @NotNull
    Mono<OfferType> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface OfferTypeRepositoryInternal {
    <S extends OfferType> Mono<S> save(S entity);

    Flux<OfferType> findAllBy(Pageable pageable);

    Flux<OfferType> findAll();

    Mono<OfferType> findById(Long id);

    Flux<OfferType> findByCriteria(OfferTypeCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(OfferTypeCriteria criteria);

    Mono<Void> deleteById(Long id);

    Flux<OfferTypeShortDTO> findAllOfferTypesForSubcategory(Long subcategoryId);
}
