package com.freelance.app.repository;

import com.freelance.app.domain.Skill;
import com.freelance.app.domain.criteria.SkillCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
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

    Mono<Void> deleteById(Long id);
}
