package com.ids.repository;

import com.ids.domain.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Article entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArticleRepository extends ReactiveCrudRepository<Article, Long>, ArticleRepositoryInternal {
    @Query("SELECT * FROM article entity WHERE entity.categorie_id = :id")
    Flux<Article> findByCategorie(Long id);

    @Query("SELECT * FROM article entity WHERE entity.categorie_id IS NULL")
    Flux<Article> findAllWhereCategorieIsNull();

    @Override
    <S extends Article> Mono<S> save(S entity);

    @Override
    Flux<Article> findAll();

    @Override
    Mono<Article> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ArticleRepositoryInternal {
    <S extends Article> Mono<S> save(S entity);

    Flux<Article> findAllBy(Pageable pageable);

    Flux<Article> findAll();

    Mono<Article> findById(Long id);

    Flux<Article> findAllBy(Pageable pageable, Criteria criteria);
}
