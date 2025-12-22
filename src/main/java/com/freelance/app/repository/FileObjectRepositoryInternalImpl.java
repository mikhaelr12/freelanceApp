package com.freelance.app.repository;

import com.freelance.app.domain.FileObject;
import com.freelance.app.domain.criteria.FileObjectCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.FileObjectRowMapper;
import com.freelance.app.repository.sqlhelper.FileObjectSqlHelper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the FileObject entity.
 */
@SuppressWarnings("unused")
class FileObjectRepositoryInternalImpl extends SimpleR2dbcRepository<FileObject, Long> implements FileObjectRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final FileObjectRowMapper fileobjectMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("file_object", EntityManager.ENTITY_ALIAS);

    public FileObjectRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        FileObjectRowMapper fileobjectMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(FileObject.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.fileobjectMapper = fileobjectMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<FileObject> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<FileObject> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = FileObjectSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, FileObject.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<FileObject> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<FileObject> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private FileObject process(Row row, RowMetadata metadata) {
        FileObject entity = fileobjectMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends FileObject> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<FileObject> findByCriteria(FileObjectCriteria fileObjectCriteria, Pageable page) {
        return createQuery(page, buildConditions(fileObjectCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(FileObjectCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(FileObjectCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getBucket() != null) {
                builder.buildFilterConditionForField(criteria.getBucket(), entityTable.column("bucket"));
            }
            if (criteria.getObjectKey() != null) {
                builder.buildFilterConditionForField(criteria.getObjectKey(), entityTable.column("object_key"));
            }
            if (criteria.getContentType() != null) {
                builder.buildFilterConditionForField(criteria.getContentType(), entityTable.column("content_type"));
            }
            if (criteria.getFileSize() != null) {
                builder.buildFilterConditionForField(criteria.getFileSize(), entityTable.column("file_size"));
            }
            if (criteria.getChecksum() != null) {
                builder.buildFilterConditionForField(criteria.getChecksum(), entityTable.column("checksum"));
            }
            if (criteria.getDurationSeconds() != null) {
                builder.buildFilterConditionForField(criteria.getDurationSeconds(), entityTable.column("duration_seconds"));
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
        }
        return builder.buildConditions();
    }
}
