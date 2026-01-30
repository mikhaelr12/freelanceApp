package com.freelance.app.repository;

import com.freelance.app.domain.FileObject;
import com.freelance.app.domain.criteria.FileObjectCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the FileObject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FileObjectRepository extends ReactiveCrudRepository<FileObject, Long>, FileObjectRepositoryInternal {
    Flux<FileObject> findAllBy(Pageable pageable);

    @Override
    <S extends FileObject> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<FileObject> findAll();

    @Override
    @NotNull
    Mono<FileObject> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface FileObjectRepositoryInternal {
    <S extends FileObject> Mono<S> save(S entity);

    Flux<FileObject> findAllBy(Pageable pageable);

    Flux<FileObject> findAll();

    Mono<FileObject> findById(Long id);

    Flux<FileObject> findByCriteria(FileObjectCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(FileObjectCriteria criteria);
}
