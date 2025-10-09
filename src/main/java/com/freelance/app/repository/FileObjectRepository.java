package com.freelance.app.repository;

import com.freelance.app.domain.FileObject;
import com.freelance.app.domain.criteria.FileObjectCriteria;
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
    <S extends FileObject> Mono<S> save(S entity);

    @Override
    Flux<FileObject> findAll();

    @Override
    Mono<FileObject> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface FileObjectRepositoryInternal {
    <S extends FileObject> Mono<S> save(S entity);

    Flux<FileObject> findAllBy(Pageable pageable);

    Flux<FileObject> findAll();

    Mono<FileObject> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<FileObject> findAllBy(Pageable pageable, Criteria criteria);
    Flux<FileObject> findByCriteria(FileObjectCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(FileObjectCriteria criteria);
}
