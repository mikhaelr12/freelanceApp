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
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.*;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
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
        this.entityManager = entityManager;
        this.fileobjectMapper = fileobjectMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<FileObject> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    DatabaseClient.GenericExecuteSpec createQuery(Pageable pageable, Condition condition, List<Expression> columns) {
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        String select = entityManager.createSelect(selectFrom, FileObject.class, pageable, condition);
        return db.sql(select);
    }

    RowsFetchSpec<FileObject> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = FileObjectSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        return createQuery(pageable, whereClause, columns).map(this::process);
    }

    @Override
    public @NotNull Flux<FileObject> findAll() {
        return findAllBy(null);
    }

    @Override
    public @NotNull Mono<FileObject> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private FileObject process(Row row, RowMetadata metadata) {
        return fileobjectMapper.apply(row, "e");
    }

    @Override
    public <S extends FileObject> @NotNull Mono<S> save(@NotNull S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<FileObject> findByCriteria(FileObjectCriteria fileObjectCriteria, Pageable page) {
        return createQuery(page, buildConditions(fileObjectCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(FileObjectCriteria criteria) {
        return createQuery(null, buildConditions(criteria), List.of(Functions.count(Expressions.asterisk())))
            .map((row, rowMetadata) -> row.get(0, Long.class))
            .one();
    }

    private Condition buildConditions(FileObjectCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<>();
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
