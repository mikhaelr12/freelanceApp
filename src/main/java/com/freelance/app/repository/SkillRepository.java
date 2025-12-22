package com.freelance.app.repository;

import com.freelance.app.domain.Skill;
import com.freelance.app.domain.criteria.SkillCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Skill entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SkillRepository extends ReactiveCrudRepository<Skill, Long>, SkillRepositoryInternal {
    Flux<Skill> findAllBy(Pageable pageable);

    @Override
    Mono<Skill> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Skill> findAllWithEagerRelationships();

    @Override
    Flux<Skill> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM skill entity WHERE entity.category_id = :id")
    Flux<Skill> findByCategory(Long id);

    @Query("SELECT * FROM skill entity WHERE entity.category_id IS NULL")
    Flux<Skill> findAllWhereCategoryIsNull();

    @Override
    <S extends Skill> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Skill> findAll();

    @Override
    @NotNull
    Mono<Skill> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface SkillRepositoryInternal {
    <S extends Skill> Mono<S> save(S entity);

    Flux<Skill> findAllBy(Pageable pageable);

    Flux<Skill> findAll();

    Mono<Skill> findById(Long id);

    Flux<Skill> findByCriteria(SkillCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(SkillCriteria criteria);

    Mono<Skill> findOneWithEagerRelationships(Long id);

    Flux<Skill> findAllWithEagerRelationships();

    Flux<Skill> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
