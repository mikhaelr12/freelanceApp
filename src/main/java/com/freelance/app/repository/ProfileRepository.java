package com.freelance.app.repository;

import com.freelance.app.domain.Profile;
import com.freelance.app.domain.criteria.ProfileCriteria;
import com.freelance.app.service.dto.ProfileDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Profile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileRepository extends ReactiveCrudRepository<Profile, Long>, ProfileRepositoryInternal {
    Flux<Profile> findAllBy(Pageable pageable);

    @Override
    Mono<Profile> findOneWithEagerRelationships(Long id);

    @Override
    <S extends Profile> Mono<S> save(S entity);

    @Override
    Flux<Profile> findAll();

    @Override
    Mono<Profile> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);

    Mono<Profile> findByUserId(Long id);
}

interface ProfileRepositoryInternal {
    <S extends Profile> Mono<S> save(S entity);

    Flux<Profile> findAllBy(Pageable pageable);

    Flux<Profile> findAll();

    Mono<Profile> findById(Long id);

    Flux<Profile> findByCriteria(ProfileCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ProfileCriteria criteria);

    Mono<Profile> findOneWithEagerRelationships(Long id);

    Flux<Profile> findAllWithEagerRelationships();

    Flux<Profile> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);

    Mono<ProfileDTO> findOne(Long id);
}
