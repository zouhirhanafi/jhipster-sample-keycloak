package com.ids.web.rest;

import com.ids.domain.Facture;
import com.ids.repository.FactureRepository;
import com.ids.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.ids.domain.Facture}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FactureResource {

    private final Logger log = LoggerFactory.getLogger(FactureResource.class);

    private static final String ENTITY_NAME = "facture";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FactureRepository factureRepository;

    public FactureResource(FactureRepository factureRepository) {
        this.factureRepository = factureRepository;
    }

    /**
     * {@code POST  /factures} : Create a new facture.
     *
     * @param facture the facture to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new facture, or with status {@code 400 (Bad Request)} if the facture has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/factures")
    public Mono<ResponseEntity<Facture>> createFacture(@RequestBody Facture facture) throws URISyntaxException {
        log.debug("REST request to save Facture : {}", facture);
        if (facture.getId() != null) {
            throw new BadRequestAlertException("A new facture cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return factureRepository
            .save(facture)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/factures/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /factures/:id} : Updates an existing facture.
     *
     * @param id the id of the facture to save.
     * @param facture the facture to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated facture,
     * or with status {@code 400 (Bad Request)} if the facture is not valid,
     * or with status {@code 500 (Internal Server Error)} if the facture couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/factures/{id}")
    public Mono<ResponseEntity<Facture>> updateFacture(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Facture facture
    ) throws URISyntaxException {
        log.debug("REST request to update Facture : {}, {}", id, facture);
        if (facture.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, facture.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return factureRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return factureRepository
                    .save(facture)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /factures/:id} : Partial updates given fields of an existing facture, field will ignore if it is null
     *
     * @param id the id of the facture to save.
     * @param facture the facture to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated facture,
     * or with status {@code 400 (Bad Request)} if the facture is not valid,
     * or with status {@code 404 (Not Found)} if the facture is not found,
     * or with status {@code 500 (Internal Server Error)} if the facture couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/factures/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Facture>> partialUpdateFacture(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Facture facture
    ) throws URISyntaxException {
        log.debug("REST request to partial update Facture partially : {}, {}", id, facture);
        if (facture.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, facture.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return factureRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Facture> result = factureRepository
                    .findById(facture.getId())
                    .map(existingFacture -> {
                        if (facture.getClient() != null) {
                            existingFacture.setClient(facture.getClient());
                        }
                        if (facture.getDate() != null) {
                            existingFacture.setDate(facture.getDate());
                        }

                        return existingFacture;
                    })
                    .flatMap(factureRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /factures} : get all the factures.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of factures in body.
     */
    @GetMapping("/factures")
    public Mono<List<Facture>> getAllFactures() {
        log.debug("REST request to get all Factures");
        return factureRepository.findAll().collectList();
    }

    /**
     * {@code GET  /factures} : get all the factures as a stream.
     * @return the {@link Flux} of factures.
     */
    @GetMapping(value = "/factures", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Facture> getAllFacturesAsStream() {
        log.debug("REST request to get all Factures as a stream");
        return factureRepository.findAll();
    }

    /**
     * {@code GET  /factures/:id} : get the "id" facture.
     *
     * @param id the id of the facture to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the facture, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/factures/{id}")
    public Mono<ResponseEntity<Facture>> getFacture(@PathVariable Long id) {
        log.debug("REST request to get Facture : {}", id);
        Mono<Facture> facture = factureRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(facture);
    }

    /**
     * {@code DELETE  /factures/:id} : delete the "id" facture.
     *
     * @param id the id of the facture to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/factures/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteFacture(@PathVariable Long id) {
        log.debug("REST request to delete Facture : {}", id);
        return factureRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
