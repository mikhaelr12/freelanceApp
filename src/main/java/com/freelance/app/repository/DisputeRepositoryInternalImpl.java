package com.freelance.app.repository;

import com.freelance.app.domain.Dispute;
import com.freelance.app.domain.criteria.DisputeCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.DisputeRowMapper;
import com.freelance.app.repository.rowmapper.OrderRowMapper;
import com.freelance.app.repository.sqlhelper.DisputeSqlHelper;
import com.freelance.app.repository.sqlhelper.OrderSqlHelper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.*;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Dispute entity.
 */
@SuppressWarnings("unused")
class DisputeRepositoryInternalImpl extends SimpleR2dbcRepository<Dispute, Long> implements DisputeRepositoryInternal {

    private final DatabaseClient db;
    private final EntityManager entityManager;
    private final OrderRowMapper orderMapper;
    private final DisputeRowMapper disputeMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("dispute", EntityManager.ENTITY_ALIAS);
    private static final Table orderTable = Table.aliased("jhi_order", "e_order");

    public DisputeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrderRowMapper orderMapper,
        DisputeRowMapper disputeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Dispute.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.entityManager = entityManager;
        this.orderMapper = orderMapper;
        this.disputeMapper = disputeMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Dispute> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    DatabaseClient.GenericExecuteSpec createQuery(Pageable pageable, Condition condition, List<Expression> columns) {
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable));
        String select = entityManager.createSelect(selectFrom, Dispute.class, pageable, condition);
        return db.sql(select);
    }

    RowsFetchSpec<Dispute> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = DisputeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OrderSqlHelper.getColumns(orderTable, "order"));
        return createQuery(pageable, whereClause, columns).map(this::process);
    }

    @Override
    public @NotNull Flux<Dispute> findAll() {
        return findAllBy(null);
    }

    @Override
    public @NotNull Mono<Dispute> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Dispute process(Row row, RowMetadata metadata) {
        Dispute entity = disputeMapper.apply(row, "e");
        entity.setOrder(orderMapper.apply(row, "order"));
        return entity;
    }

    @Override
    public <S extends Dispute> @NotNull Mono<S> save(@NotNull S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Dispute> findByCriteria(DisputeCriteria disputeCriteria, Pageable page) {
        return createQuery(page, buildConditions(disputeCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(DisputeCriteria criteria) {
        return createQuery(null, buildConditions(criteria), List.of(Functions.count(Expressions.asterisk())))
            .map((row, rowMetadata) -> row.get(0, Long.class))
            .one();
    }

    private Condition buildConditions(DisputeCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getReason() != null) {
                builder.buildFilterConditionForField(criteria.getReason(), entityTable.column("reason"));
            }
            if (criteria.getOpenedAt() != null) {
                builder.buildFilterConditionForField(criteria.getOpenedAt(), entityTable.column("opened_at"));
            }
            if (criteria.getClosedAt() != null) {
                builder.buildFilterConditionForField(criteria.getClosedAt(), entityTable.column("closed_at"));
            }
            if (criteria.getOrderId() != null) {
                builder.buildFilterConditionForField(criteria.getOrderId(), orderTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
