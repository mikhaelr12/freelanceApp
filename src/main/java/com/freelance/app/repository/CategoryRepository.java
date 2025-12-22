package com.freelance.app.repository;

import com.freelance.app.domain.Category;
import com.freelance.app.domain.criteria.CategoryCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Category entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryRepository extends ReactiveCrudRepository<Category, Long>, CategoryRepositoryInternal {
    Flux<Category> findAllBy(Pageable pageable);

    @Override
    <S extends Category> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Category> findAll();

    @Override
    @NotNull
    Mono<Category> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface CategoryRepositoryInternal {
    <S extends Category> Mono<S> save(S entity);

    Flux<Category> findAllBy(Pageable pageable);

    Flux<Category> findAll();

    Mono<Category> findById(Long id);

    Flux<Category> findByCriteria(CategoryCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(CategoryCriteria criteria);
}
