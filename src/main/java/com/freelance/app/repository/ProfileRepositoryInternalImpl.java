package com.freelance.app.repository;

import com.freelance.app.domain.Profile;
import com.freelance.app.domain.Skill;
import com.freelance.app.domain.criteria.ProfileCriteria;
import com.freelance.app.repository.rowmapper.*;
import com.freelance.app.repository.sqlhelper.FileObjectSqlHelper;
import com.freelance.app.repository.sqlhelper.ProfileSqlHelper;
import com.freelance.app.repository.sqlhelper.SkillSqlHelper;
import com.freelance.app.repository.sqlhelper.UserSqlHelper;
import com.freelance.app.service.dto.ProfileDTO;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
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
 * Spring Data R2DBC custom repository implementation for the Profile entity.
 */
@SuppressWarnings("unused")
class ProfileRepositoryInternalImpl extends SimpleR2dbcRepository<Profile, Long> implements ProfileRepositoryInternal {

    private final DatabaseClient db;
    private final EntityManager entityManager;
    private final UserRowMapper userMapper;
    private final FileObjectRowMapper fileobjectMapper;
    private final ProfileRowMapper profileMapper;
    private final ColumnConverter columnConverter;
    private final SkillRowMapper skillMapper;

    private static final Table entityTable = Table.aliased("profile", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table profilePictureTable = Table.aliased("file_object", "profilePicture");
    private static final Table skillTable = Table.aliased("skill", "skill");

    private static final EntityManager.LinkTable skillLink = new EntityManager.LinkTable("rel_profile__skill", "profile_id", "skill_id");

    public ProfileRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        FileObjectRowMapper fileobjectMapper,
        ProfileRowMapper profileMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter,
        SkillRowMapper skillMapper
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Profile.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.fileobjectMapper = fileobjectMapper;
        this.profileMapper = profileMapper;
        this.columnConverter = columnConverter;
        this.skillMapper = skillMapper;
    }

    @Override
    public Flux<Profile> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    DatabaseClient.GenericExecuteSpec createQuery(Pageable pageable, Condition condition, List<Expression> columns) {
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(profilePictureTable)
            .on(Column.create("profile_picture_id", entityTable))
            .equals(Column.create("id", profilePictureTable));
        String select = entityManager.createSelect(selectFrom, Profile.class, pageable, condition);
        return db.sql(select);
    }

    RowsFetchSpec<Profile> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProfileSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(FileObjectSqlHelper.getColumns(profilePictureTable, "profilePicture"));
        return createQuery(pageable, whereClause, columns).map(this::process);
    }

    @Override
    public @NotNull Flux<Profile> findAll() {
        return findAllBy(null);
    }

    @Override
    public @NotNull Mono<Profile> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Profile> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Profile> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Profile> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Profile process(Row row, RowMetadata metadata) {
        Profile entity = profileMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setProfilePicture(fileobjectMapper.apply(row, "profilePicture"));
        return entity;
    }

    @Override
    public <S extends Profile> @NotNull Mono<S> save(@NotNull S entity) {
        return super.save(entity).flatMap(this::updateRelations);
    }

    protected <S extends Profile> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager.updateLinkTable(skillLink, entity.getId(), entity.getSkills().stream().map(Skill::getId)).then();
        return result.thenReturn(entity);
    }

    @Override
    public @NotNull Mono<Void> deleteById(@NotNull Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    @Override
    public Mono<ProfileDTO> findOne(Long id) {
        return findByIdDTO(id).flatMap(this::fetchSkills);
    }

    private Mono<ProfileDTO> findByIdDTO(Long id) {
        String columns = ProfileSqlHelper.getColumnsShortDTO(entityTable, EntityManager.ENTITY_ALIAS)
            .stream()
            .map(Expression::toString)
            .collect(Collectors.joining(", "));

        String sql = "SELECT " + columns + " " + "FROM profile e " + "WHERE e.id = :id";
        return db.sql(sql).bind("id", id).map((row, rowMetadata) -> profileMapper.applyDTO(row, "e")).one();
    }

    private Mono<ProfileDTO> fetchSkills(ProfileDTO entity) {
        String columns = SkillSqlHelper.getColumnsShort(skillTable, "skill")
            .stream()
            .map(Expression::toString)
            .collect(Collectors.joining(", "));

        String sql = "SELECT " + columns + " " + "FROM skill skill " + "JOIN rel_profile__skill sk ON sk.profile_id = :profileId";
        return db
            .sql(sql)
            .bind("profileId", entity.getId())
            .map((row, rowMetadata) -> skillMapper.applyShort(row, "skill"))
            .all()
            .collectList()
            .map(skills -> {
                entity.setSkills(new HashSet<>(skills));
                return entity;
            });
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(skillLink, entityId);
    }

    @Override
    public Flux<Profile> findByCriteria(ProfileCriteria profileCriteria, Pageable page) {
        return createQuery(page, buildConditions(profileCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ProfileCriteria criteria) {
        return createQuery(null, buildConditions(criteria), List.of(Functions.count(Expressions.asterisk())))
            .map((row, rowMetadata) -> row.get(0, Long.class))
            .one();
    }

    private Condition buildConditions(ProfileCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getFirstName() != null) {
                builder.buildFilterConditionForField(criteria.getFirstName(), entityTable.column("first_name"));
            }
            if (criteria.getLastName() != null) {
                builder.buildFilterConditionForField(criteria.getLastName(), entityTable.column("last_name"));
            }
            if (criteria.getDescription() != null) {
                builder.buildFilterConditionForField(criteria.getDescription(), entityTable.column("description"));
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
            if (criteria.getProfileType() != null) {
                builder.buildFilterConditionForField(criteria.getProfileType(), entityTable.column("profile_type"));
            }
            if (criteria.getUserId() != null) {
                builder.buildFilterConditionForField(criteria.getUserId(), userTable.column("id"));
            }
            if (criteria.getProfilePictureId() != null) {
                builder.buildFilterConditionForField(criteria.getProfilePictureId(), profilePictureTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
