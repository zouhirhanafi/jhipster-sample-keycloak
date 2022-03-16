package com.ids.repository;

import com.ids.domain.Facture;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Facture entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FactureRepository extends ReactiveCrudRepository<Facture, Long>, FactureRepositoryInternal {
    @Override
    <S extends Facture> Mono<S> save(S entity);

    @Override
    Flux<Facture> findAll();

    @Override
    Mono<Facture> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface FactureRepositoryInternal {
    <S extends Facture> Mono<S> save(S entity);

    Flux<Facture> findAllBy(Pageable pageable);

    Flux<Facture> findAll();

    Mono<Facture> findById(Long id);

    Flux<Facture> findAllBy(Pageable pageable, Criteria criteria);
}
