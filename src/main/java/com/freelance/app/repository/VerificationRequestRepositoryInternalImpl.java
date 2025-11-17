package com.freelance.app.repository;

import com.freelance.app.domain.VerificationRequest;
import com.freelance.app.domain.criteria.VerificationRequestCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.FileObjectRowMapper;
import com.freelance.app.repository.rowmapper.ProfileRowMapper;
import com.freelance.app.repository.rowmapper.VerificationRequestRowMapper;
import com.freelance.app.repository.sqlhelper.FileObjectSqlHelper;
import com.freelance.app.repository.sqlhelper.ProfileSqlHelper;
import com.freelance.app.repository.sqlhelper.VerificationRequestSqlHelper;
import com.freelance.app.service.dto.VerificationRequestDTO;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.aspectj.weaver.ast.Expr;
import org.springframework.data.domain.Page;
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
 * Spring Data R2DBC custom repository implementation for the VerificationRequest entity.
 */
@SuppressWarnings("unused")
class VerificationRequestRepositoryInternalImpl
    extends SimpleR2dbcRepository<VerificationRequest, Long>
    implements VerificationRequestRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProfileRowMapper profileMapper;
    private final FileObjectRowMapper fileobjectMapper;
    private final VerificationRequestRowMapper verificationrequestMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("verification_request", EntityManager.ENTITY_ALIAS);
    private static final Table profileTable = Table.aliased("profile", "e_profile");
    private static final Table fileObjectTable = Table.aliased("file_object", "fileObject");

    public VerificationRequestRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProfileRowMapper profileMapper,
        FileObjectRowMapper fileobjectMapper,
        VerificationRequestRowMapper verificationrequestMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(VerificationRequest.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.profileMapper = profileMapper;
        this.fileobjectMapper = fileobjectMapper;
        this.verificationrequestMapper = verificationrequestMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<VerificationRequest> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<VerificationRequest> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = VerificationRequestSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProfileSqlHelper.getColumns(profileTable, "profile"));
        columns.addAll(FileObjectSqlHelper.getColumns(fileObjectTable, "fileObject"));
        return createQuery(pageable, whereClause, columns).map(this::process);
    }

    DatabaseClient.GenericExecuteSpec createQuery(Pageable pageable, Condition whereClause, List<Expression> columns) {
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(profileTable)
            .on(Column.create("profile_id", entityTable))
            .equals(Column.create("id", profileTable))
            .leftOuterJoin(fileObjectTable)
            .on(Column.create("file_object_id", entityTable))
            .equals(Column.create("id", fileObjectTable));
        String select = entityManager.createSelect(selectFrom, VerificationRequest.class, pageable, whereClause);
        return db.sql(select);
    }

    @Override
    public Flux<VerificationRequest> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<VerificationRequest> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private VerificationRequest process(Row row, RowMetadata metadata) {
        VerificationRequest entity = verificationrequestMapper.apply(row, "e");
        entity.setProfile(profileMapper.apply(row, "profile"));
        entity.setFileObject(fileobjectMapper.apply(row, "fileObject"));
        return entity;
    }

    @Override
    public <S extends VerificationRequest> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<VerificationRequest> findByCriteria(VerificationRequestCriteria verificationRequestCriteria, Pageable page) {
        return createQuery(page, buildConditions(verificationRequestCriteria)).all();
    }

    @Override
    public Flux<VerificationRequestDTO> findByCriteriaDTO(VerificationRequestCriteria criteria, Pageable pageable) {
        List<Expression> columns = VerificationRequestSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        return createQuery(pageable, buildConditions(criteria), columns)
            .map((row, rowMetadata) -> verificationrequestMapper.applyDTO(row, "e"))
            .all();
    }

    @Override
    public Mono<Long> countByCriteria(VerificationRequestCriteria criteria) {
        return createCountQuery(buildConditions(criteria)).one();
    }

    RowsFetchSpec<Long> createCountQuery(Condition whereClause) {
        return createQuery(null, whereClause, List.of(Functions.count(Expressions.asterisk()))).map((row, rowMetadata) ->
            row.get(0, Long.class)
        );
    }

    private Condition buildConditions(VerificationRequestCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getProfileId() != null) {
                builder.buildFilterConditionForField(criteria.getProfileId(), profileTable.column("id"));
            }
            if (criteria.getFileObjectId() != null) {
                builder.buildFilterConditionForField(criteria.getFileObjectId(), fileObjectTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
