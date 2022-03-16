package com.ids.repository;

import com.ids.domain.Commande;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Commande entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommandeRepository extends ReactiveCrudRepository<Commande, Long>, CommandeRepositoryInternal {
    @Override
    <S extends Commande> Mono<S> save(S entity);

    @Override
    Flux<Commande> findAll();

    @Override
    Mono<Commande> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CommandeRepositoryInternal {
    <S extends Commande> Mono<S> save(S entity);

    Flux<Commande> findAllBy(Pageable pageable);

    Flux<Commande> findAll();

    Mono<Commande> findById(Long id);

    Flux<Commande> findAllBy(Pageable pageable, Criteria criteria);
}
