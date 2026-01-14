package com.freelance.app.repository;

import com.freelance.app.domain.Conversation;
import com.freelance.app.domain.criteria.ConversationCriteria;
import org.jetbrains.annotations.NotNull;
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
    <S extends Conversation> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Conversation> findAll();

    @Override
    @NotNull
    Mono<Conversation> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);

    @Query(
        """
        SELECT * FROM conversation
                WHERE (participant_a_id = :participantAId AND participant_b_id = :participantBId)
                   OR (participant_a_id = :participantBId AND participant_b_id = :participantAId)
                LIMIT 1
        """
    )
    Mono<Conversation> findBetween(Long participantAId, Long participantBId);
}

interface ConversationRepositoryInternal {
    <S extends Conversation> Mono<S> save(S entity);

    Flux<Conversation> findAllBy(Pageable pageable);

    Flux<Conversation> findAll();

    Mono<Conversation> findById(Long id);

    Flux<Conversation> findByCriteria(ConversationCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ConversationCriteria criteria);
}
