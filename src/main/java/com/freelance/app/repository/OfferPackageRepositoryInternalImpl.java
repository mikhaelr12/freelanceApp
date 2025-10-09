package com.freelance.app.repository;

import com.freelance.app.domain.OfferPackage;
import com.freelance.app.domain.criteria.OfferPackageCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.OfferPackageRowMapper;
import com.freelance.app.repository.rowmapper.OfferRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the OfferPackage entity.
 */
@SuppressWarnings("unused")
class OfferPackageRepositoryInternalImpl extends SimpleR2dbcRepository<OfferPackage, Long> implements OfferPackageRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OfferRowMapper offerMapper;
    private final OfferPackageRowMapper offerpackageMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("offer_package", EntityManager.ENTITY_ALIAS);
    private static final Table offerTable = Table.aliased("offer", "offer");

    public OfferPackageRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OfferRowMapper offerMapper,
        OfferPackageRowMapper offerpackageMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(OfferPackage.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.offerMapper = offerMapper;
        this.offerpackageMapper = offerpackageMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<OfferPackage> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<OfferPackage> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OfferPackageSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OfferSqlHelper.getColumns(offerTable, "offer"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(offerTable)
            .on(Column.create("offer_id", entityTable))
            .equals(Column.create("id", offerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, OfferPackage.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<OfferPackage> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<OfferPackage> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<OfferPackage> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<OfferPackage> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<OfferPackage> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private OfferPackage process(Row row, RowMetadata metadata) {
        OfferPackage entity = offerpackageMapper.apply(row, "e");
        entity.setOffer(offerMapper.apply(row, "offer"));
        return entity;
    }

    @Override
    public <S extends OfferPackage> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<OfferPackage> findByCriteria(OfferPackageCriteria offerPackageCriteria, Pageable page) {
        return createQuery(page, buildConditions(offerPackageCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(OfferPackageCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(OfferPackageCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getDescription() != null) {
                builder.buildFilterConditionForField(criteria.getDescription(), entityTable.column("description"));
            }
            if (criteria.getPrice() != null) {
                builder.buildFilterConditionForField(criteria.getPrice(), entityTable.column("price"));
            }
            if (criteria.getCurrency() != null) {
                builder.buildFilterConditionForField(criteria.getCurrency(), entityTable.column("currency"));
            }
            if (criteria.getDeliveryDays() != null) {
                builder.buildFilterConditionForField(criteria.getDeliveryDays(), entityTable.column("delivery_days"));
            }
            if (criteria.getPackageTier() != null) {
                builder.buildFilterConditionForField(criteria.getPackageTier(), entityTable.column("package_tier"));
            }
            if (criteria.getActive() != null) {
                builder.buildFilterConditionForField(criteria.getActive(), entityTable.column("active"));
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
            if (criteria.getOfferId() != null) {
                builder.buildFilterConditionForField(criteria.getOfferId(), offerTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
