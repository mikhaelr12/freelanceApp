package com.freelance.app.repository;

import com.freelance.app.domain.OfferType;
import com.freelance.app.domain.criteria.OfferTypeCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.OfferTypeRowMapper;
import com.freelance.app.repository.rowmapper.SubcategoryRowMapper;
import com.freelance.app.repository.sqlhelper.OfferTypeSqlHelper;
import com.freelance.app.repository.sqlhelper.SubcategorySqlHelper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
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
 * Spring Data R2DBC custom repository implementation for the OfferType entity.
 */
@SuppressWarnings("unused")
class OfferTypeRepositoryInternalImpl extends SimpleR2dbcRepository<OfferType, Long> implements OfferTypeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final SubcategoryRowMapper subcategoryMapper;
    private final OfferTypeRowMapper offertypeMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("offer_type", EntityManager.ENTITY_ALIAS);
    private static final Table subcategoryTable = Table.aliased("subcategory", "subcategory");

    public OfferTypeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        SubcategoryRowMapper subcategoryMapper,
        OfferTypeRowMapper offertypeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(OfferType.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.subcategoryMapper = subcategoryMapper;
        this.offertypeMapper = offertypeMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<OfferType> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<OfferType> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OfferTypeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(SubcategorySqlHelper.getColumns(subcategoryTable, "subcategory"));
        return createQuery(pageable, whereClause, columns).map(this::process);
    }

    DatabaseClient.GenericExecuteSpec createQuery(Pageable pageable, Condition whereClause, List<Expression> columns) {
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(subcategoryTable)
            .on(Column.create("subcategory_id", entityTable))
            .equals(Column.create("id", subcategoryTable));
        String select = entityManager.createSelect(selectFrom, OfferType.class, pageable, whereClause);
        return db.sql(select);
    }

    @Override
    public Flux<OfferType> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<OfferType> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<OfferType> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<OfferType> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<OfferType> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private OfferType process(Row row, RowMetadata metadata) {
        OfferType entity = offertypeMapper.apply(row, "e");
        entity.setSubcategory(subcategoryMapper.apply(row, "subcategory"));
        return entity;
    }

    @Override
    public <S extends OfferType> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<OfferType> findByCriteria(OfferTypeCriteria offerTypeCriteria, Pageable page) {
        return createQuery(page, buildConditions(offerTypeCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(OfferTypeCriteria criteria) {
        return createCountQuery(buildConditions(criteria)).one();
    }

    private RowsFetchSpec<Long> createCountQuery(Condition whereClause) {
        return createQuery(null, whereClause, List.of(Functions.count(Expressions.asterisk()))).map((row, metadata) ->
            row.get(0, Long.class)
        );
    }

    private Condition buildConditions(OfferTypeCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getCreatedDate() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedDate(), entityTable.column("created_date"));
            }
            if (criteria.getLastModifiedDate() != null) {
                builder.buildFilterConditionForField(criteria.getLastModifiedDate(), entityTable.column("last_modified_date"));
            }
            if (criteria.getCreatedBy() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedBy(), entityTable.column("created_by"));
            }
            if (criteria.getLastModifiedBy() != null) {
                builder.buildFilterConditionForField(criteria.getLastModifiedBy(), entityTable.column("last_modified_by"));
            }
            if (criteria.getActive() != null) {
                builder.buildFilterConditionForField(criteria.getActive(), entityTable.column("active"));
            }
            if (criteria.getSubcategoryId() != null) {
                builder.buildFilterConditionForField(criteria.getSubcategoryId(), subcategoryTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
