package com.freelance.app.repository;

import com.freelance.app.domain.OfferMedia;
import com.freelance.app.domain.criteria.OfferMediaCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.FileObjectRowMapper;
import com.freelance.app.repository.rowmapper.OfferMediaRowMapper;
import com.freelance.app.repository.rowmapper.OfferRowMapper;
import com.freelance.app.repository.sqlhelper.FileObjectSqlHelper;
import com.freelance.app.repository.sqlhelper.OfferMediaSqlHelper;
import com.freelance.app.repository.sqlhelper.OfferSqlHelper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the OfferMedia entity.
 */
@SuppressWarnings("unused")
class OfferMediaRepositoryInternalImpl extends SimpleR2dbcRepository<OfferMedia, Long> implements OfferMediaRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OfferRowMapper offerMapper;
    private final FileObjectRowMapper fileobjectMapper;
    private final OfferMediaRowMapper offermediaMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("offer_media", EntityManager.ENTITY_ALIAS);
    private static final Table offerTable = Table.aliased("offer", "offer");
    private static final Table fileTable = Table.aliased("file_object", "e_file");

    public OfferMediaRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OfferRowMapper offerMapper,
        FileObjectRowMapper fileobjectMapper,
        OfferMediaRowMapper offermediaMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(OfferMedia.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.offerMapper = offerMapper;
        this.fileobjectMapper = fileobjectMapper;
        this.offermediaMapper = offermediaMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<OfferMedia> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<OfferMedia> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OfferMediaSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OfferSqlHelper.getColumns(offerTable, "offer"));
        columns.addAll(FileObjectSqlHelper.getColumns(fileTable, "file"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(offerTable)
            .on(Column.create("offer_id", entityTable))
            .equals(Column.create("id", offerTable))
            .leftOuterJoin(fileTable)
            .on(Column.create("file_id", entityTable))
            .equals(Column.create("id", fileTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, OfferMedia.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<OfferMedia> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<OfferMedia> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<OfferMedia> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<OfferMedia> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<OfferMedia> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private OfferMedia process(Row row, RowMetadata metadata) {
        OfferMedia entity = offermediaMapper.apply(row, "e");
        entity.setOffer(offerMapper.apply(row, "offer"));
        entity.setFile(fileobjectMapper.apply(row, "file"));
        return entity;
    }

    @Override
    public <S extends OfferMedia> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<OfferMedia> findByCriteria(OfferMediaCriteria offerMediaCriteria, Pageable page) {
        return createQuery(page, buildConditions(offerMediaCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(OfferMediaCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(OfferMediaCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getMediaKind() != null) {
                builder.buildFilterConditionForField(criteria.getMediaKind(), entityTable.column("media_kind"));
            }
            if (criteria.getIsPrimary() != null) {
                builder.buildFilterConditionForField(criteria.getIsPrimary(), entityTable.column("is_primary"));
            }
            if (criteria.getCaption() != null) {
                builder.buildFilterConditionForField(criteria.getCaption(), entityTable.column("caption"));
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
            if (criteria.getFileId() != null) {
                builder.buildFilterConditionForField(criteria.getFileId(), fileTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
