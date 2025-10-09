package com.freelance.app.repository;

import com.freelance.app.domain.Conversation;
import com.freelance.app.domain.criteria.ConversationCriteria;
import com.freelance.app.service.dto.ConversationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Conversation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConversationRepository extends ReactiveCrudRepository<Conversation, Long>, ConversationRepositoryInternal {
    Flux<Conversation> findAllBy(Pageable pageable);

    @Query("SELECT * FROM conversation entity WHERE entity.order_id = :id")
    Flux<Conversation> findByOrder(Long id);

    @Query("SELECT * FROM conversation entity WHERE entity.order_id IS NULL")
    Flux<Conversation> findAllWhereOrderIsNull();

    @Override
    <S extends Conversation> Mono<S> save(S entity);

    @Override
    Flux<Conversation> findAll();

    @Override
    Mono<Conversation> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ConversationRepositoryInternal {
    <S extends Conversation> Mono<S> save(S entity);

    Flux<Conversation> findAllBy(Pageable pageable);

    Flux<Conversation> findAll();

    Mono<Conversation> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Conversation> findAllBy(Pageable pageable, Criteria criteria);
    Flux<ConversationDTO> findByCriteria(ConversationCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ConversationCriteria criteria);

    Mono<ConversationDTO> findDTOById(Long id);
}
