package com.freelance.app.repository;

import com.freelance.app.domain.Requirement;
import com.freelance.app.domain.criteria.RequirementCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.OrderRowMapper;
import com.freelance.app.repository.rowmapper.RequirementRowMapper;
import com.freelance.app.repository.sqlhelper.OrderSqlHelper;
import com.freelance.app.repository.sqlhelper.RequirementSqlHelper;
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
 * Spring Data R2DBC custom repository implementation for the Requirement entity.
 */
@SuppressWarnings("unused")
class RequirementRepositoryInternalImpl extends SimpleR2dbcRepository<Requirement, Long> implements RequirementRepositoryInternal {

    private final DatabaseClient db;
    private final EntityManager entityManager;
    private final OrderRowMapper orderMapper;
    private final RequirementRowMapper requirementMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("requirement", EntityManager.ENTITY_ALIAS);
    private static final Table orderTable = Table.aliased("jhi_order", "e_order");

    public RequirementRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrderRowMapper orderMapper,
        RequirementRowMapper requirementMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Requirement.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.entityManager = entityManager;
        this.orderMapper = orderMapper;
        this.requirementMapper = requirementMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Requirement> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Requirement> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RequirementSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OrderSqlHelper.getColumns(orderTable, "order"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable));
        String select = entityManager.createSelect(selectFrom, Requirement.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public @NotNull Flux<Requirement> findAll() {
        return findAllBy(null);
    }

    @Override
    public @NotNull Mono<Requirement> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Requirement process(Row row, RowMetadata metadata) {
        Requirement entity = requirementMapper.apply(row, "e");
        entity.setOrder(orderMapper.apply(row, "order"));
        return entity;
    }

    @Override
    public <S extends Requirement> @NotNull Mono<S> save(@NotNull S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Requirement> findByCriteria(RequirementCriteria requirementCriteria, Pageable page) {
        return createQuery(page, buildConditions(requirementCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(RequirementCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(RequirementCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getPrompt() != null) {
                builder.buildFilterConditionForField(criteria.getPrompt(), entityTable.column("prompt"));
            }
            if (criteria.getAnswer() != null) {
                builder.buildFilterConditionForField(criteria.getAnswer(), entityTable.column("answer"));
            }
            if (criteria.getOrderId() != null) {
                builder.buildFilterConditionForField(criteria.getOrderId(), orderTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
