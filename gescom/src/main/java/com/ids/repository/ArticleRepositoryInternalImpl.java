package com.ids.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.ids.domain.Article;
import com.ids.repository.rowmapper.ArticleRowMapper;
import com.ids.repository.rowmapper.CategorieRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Article entity.
 */
@SuppressWarnings("unused")
class ArticleRepositoryInternalImpl extends SimpleR2dbcRepository<Article, Long> implements ArticleRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CategorieRowMapper categorieMapper;
    private final ArticleRowMapper articleMapper;

    private static final Table entityTable = Table.aliased("article", EntityManager.ENTITY_ALIAS);
    private static final Table categorieTable = Table.aliased("categorie", "categorie");

    public ArticleRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CategorieRowMapper categorieMapper,
        ArticleRowMapper articleMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Article.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.categorieMapper = categorieMapper;
        this.articleMapper = articleMapper;
    }

    @Override
    public Flux<Article> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Article> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Article> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ArticleSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CategorieSqlHelper.getColumns(categorieTable, "categorie"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(categorieTable)
            .on(Column.create("categorie_id", entityTable))
            .equals(Column.create("id", categorieTable));

        String select = entityManager.createSelect(selectFrom, Article.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Article> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Article> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private Article process(Row row, RowMetadata metadata) {
        Article entity = articleMapper.apply(row, "e");
        entity.setCategorie(categorieMapper.apply(row, "categorie"));
        return entity;
    }

    @Override
    public <S extends Article> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
