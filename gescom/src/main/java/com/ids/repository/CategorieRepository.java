package com.ids.repository;

import com.ids.domain.Categorie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Categorie entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategorieRepository extends ReactiveCrudRepository<Categorie, Long>, CategorieRepositoryInternal {
    @Override
    <S extends Categorie> Mono<S> save(S entity);

    @Override
    Flux<Categorie> findAll();

    @Override
    Mono<Categorie> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CategorieRepositoryInternal {
    <S extends Categorie> Mono<S> save(S entity);

    Flux<Categorie> findAllBy(Pageable pageable);

    Flux<Categorie> findAll();

    Mono<Categorie> findById(Long id);

    Flux<Categorie> findAllBy(Pageable pageable, Criteria criteria);
}
