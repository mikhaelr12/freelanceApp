package com.freelance.app.repository;

import com.freelance.app.domain.Order;
import com.freelance.app.domain.criteria.OrderCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long>, OrderRepositoryInternal {
    Flux<Order> findAllBy(Pageable pageable);

    @Override
    Mono<Order> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Order> findAllWithEagerRelationships();

    @Override
    Flux<Order> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM jhi_order entity WHERE entity.buyer_id = :id")
    Flux<Order> findByBuyer(Long id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.buyer_id IS NULL")
    Flux<Order> findAllWhereBuyerIsNull();

    @Query("SELECT * FROM jhi_order entity WHERE entity.seller_id = :id")
    Flux<Order> findBySeller(Long id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.seller_id IS NULL")
    Flux<Order> findAllWhereSellerIsNull();

    @Query("SELECT * FROM jhi_order entity WHERE entity.offerpackage_id = :id")
    Flux<Order> findByOfferpackage(Long id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.offerpackage_id IS NULL")
    Flux<Order> findAllWhereOfferpackageIsNull();

    @Override
    <S extends Order> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Order> findAll();

    @Override
    @NotNull
    Mono<Order> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);
}

interface OrderRepositoryInternal {
    <S extends Order> Mono<S> save(S entity);

    Flux<Order> findAllBy(Pageable pageable);

    Flux<Order> findAll();

    Mono<Order> findById(Long id);

    Flux<Order> findByCriteria(OrderCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(OrderCriteria criteria);

    Mono<Order> findOneWithEagerRelationships(Long id);

    Flux<Order> findAllWithEagerRelationships();

    Flux<Order> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
