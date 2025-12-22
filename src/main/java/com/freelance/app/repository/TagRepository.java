package com.freelance.app.repository;

import com.freelance.app.domain.Tag;
import com.freelance.app.domain.criteria.TagCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Tag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagRepository extends ReactiveCrudRepository<Tag, Long>, TagRepositoryInternal {
    Flux<Tag> findAllBy(Pageable pageable);

    @Override
    <S extends Tag> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Tag> findAll();

    @Override
    @NotNull
    Mono<Tag> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface TagRepositoryInternal {
    <S extends Tag> Mono<S> save(S entity);

    Flux<Tag> findAllBy(Pageable pageable);

    Flux<Tag> findAll();

    Mono<Tag> findById(Long id);

    Flux<Tag> findByCriteria(TagCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TagCriteria criteria);
}
