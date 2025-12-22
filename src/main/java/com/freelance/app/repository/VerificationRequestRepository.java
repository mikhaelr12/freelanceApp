package com.freelance.app.repository;

import com.freelance.app.domain.VerificationRequest;
import com.freelance.app.domain.criteria.VerificationRequestCriteria;
import com.freelance.app.service.dto.VerificationRequestDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the VerificationRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VerificationRequestRepository
    extends ReactiveCrudRepository<VerificationRequest, Long>, VerificationRequestRepositoryInternal {
    Flux<VerificationRequest> findAllBy(Pageable pageable);

    @Override
    <S extends VerificationRequest> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<VerificationRequest> findAll();

    @Override
    @NotNull
    Mono<VerificationRequest> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);

    Mono<VerificationRequest> findByProfileId(Long id);
}

interface VerificationRequestRepositoryInternal {
    <S extends VerificationRequest> Mono<S> save(S entity);

    Flux<VerificationRequest> findAllBy(Pageable pageable);

    Flux<VerificationRequest> findAll();

    Mono<VerificationRequest> findById(Long id);

    Flux<VerificationRequest> findByCriteria(VerificationRequestCriteria criteria, Pageable pageable);

    Flux<VerificationRequestDTO> findByCriteriaDTO(VerificationRequestCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(VerificationRequestCriteria criteria);
}
