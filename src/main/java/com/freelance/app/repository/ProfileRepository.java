package com.freelance.app.repository;

import com.freelance.app.domain.Profile;
import com.freelance.app.domain.criteria.ProfileCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
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
    Flux<Profile> findAllWithEagerRelationships();

    @Override
    Flux<Profile> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM profile entity WHERE entity.user_id = :id")
    Flux<Profile> findByUser(Long id);

    @Query("SELECT * FROM profile entity WHERE entity.user_id IS NULL")
    Flux<Profile> findAllWhereUserIsNull();

    @Query("SELECT * FROM profile entity WHERE entity.profile_picture_id = :id")
    Flux<Profile> findByProfilePicture(Long id);

    @Query("SELECT * FROM profile entity WHERE entity.profile_picture_id IS NULL")
    Flux<Profile> findAllWhereProfilePictureIsNull();

    @Query(
        "SELECT entity.* FROM profile entity JOIN rel_profile__skill joinTable ON entity.id = joinTable.skill_id WHERE joinTable.skill_id = :id"
    )
    Flux<Profile> findBySkill(Long id);

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
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Profile> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Profile> findByCriteria(ProfileCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ProfileCriteria criteria);

    Mono<Profile> findOneWithEagerRelationships(Long id);

    Flux<Profile> findAllWithEagerRelationships();

    Flux<Profile> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
