package com.freelance.app.repository;

import com.freelance.app.domain.Subcategory;
import com.freelance.app.domain.criteria.SubcategoryCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Subcategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubcategoryRepository extends ReactiveCrudRepository<Subcategory, Long>, SubcategoryRepositoryInternal {
    Flux<Subcategory> findAllBy(Pageable pageable);

    @Override
    Mono<Subcategory> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Subcategory> findAllWithEagerRelationships();

    @Override
    Flux<Subcategory> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM subcategory entity WHERE entity.category_id = :id")
    Flux<Subcategory> findByCategory(Long id);

    @Query("SELECT * FROM subcategory entity WHERE entity.category_id IS NULL")
    Flux<Subcategory> findAllWhereCategoryIsNull();

    @Override
    <S extends Subcategory> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Subcategory> findAll();

    @Override
    @NotNull
    Mono<Subcategory> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface SubcategoryRepositoryInternal {
    <S extends Subcategory> Mono<S> save(S entity);

    Flux<Subcategory> findAllBy(Pageable pageable);

    Flux<Subcategory> findAll();

    Mono<Subcategory> findById(Long id);

    Flux<Subcategory> findByCriteria(SubcategoryCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(SubcategoryCriteria criteria);

    Mono<Subcategory> findOneWithEagerRelationships(Long id);

    Flux<Subcategory> findAllWithEagerRelationships();

    Flux<Subcategory> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
