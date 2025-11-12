package com.freelance.app.repository;

import com.freelance.app.domain.VerificationRequest;
import com.freelance.app.domain.criteria.VerificationRequestCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
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

    @Query("SELECT * FROM verification_request entity WHERE entity.profile_id = :id")
    Flux<VerificationRequest> findByProfile(Long id);

    @Query("SELECT * FROM verification_request entity WHERE entity.profile_id IS NULL")
    Flux<VerificationRequest> findAllWhereProfileIsNull();

    @Query("SELECT * FROM verification_request entity WHERE entity.file_object_id = :id")
    Flux<VerificationRequest> findByFileObject(Long id);

    @Query("SELECT * FROM verification_request entity WHERE entity.file_object_id IS NULL")
    Flux<VerificationRequest> findAllWhereFileObjectIsNull();

    @Override
    <S extends VerificationRequest> Mono<S> save(S entity);

    @Override
    Flux<VerificationRequest> findAll();

    @Override
    Mono<VerificationRequest> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface VerificationRequestRepositoryInternal {
    <S extends VerificationRequest> Mono<S> save(S entity);

    Flux<VerificationRequest> findAllBy(Pageable pageable);

    Flux<VerificationRequest> findAll();

    Mono<VerificationRequest> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<VerificationRequest> findAllBy(Pageable pageable, Criteria criteria);
    Flux<VerificationRequest> findByCriteria(VerificationRequestCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(VerificationRequestCriteria criteria);
}
