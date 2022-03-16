package com.ids.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.ids.IntegrationTest;
import com.ids.domain.Facture;
import com.ids.repository.EntityManager;
import com.ids.repository.FactureRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link FactureResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class FactureResourceIT {

    private static final Long DEFAULT_CLIENT = 1L;
    private static final Long UPDATED_CLIENT = 2L;

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/factures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Facture facture;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Facture createEntity(EntityManager em) {
        Facture facture = new Facture().client(DEFAULT_CLIENT).date(DEFAULT_DATE);
        return facture;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Facture createUpdatedEntity(EntityManager em) {
        Facture facture = new Facture().client(UPDATED_CLIENT).date(UPDATED_DATE);
        return facture;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Facture.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        facture = createEntity(em);
    }

    @Test
    void createFacture() throws Exception {
        int databaseSizeBeforeCreate = factureRepository.findAll().collectList().block().size();
        // Create the Facture
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facture))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Facture in the database
        List<Facture> factureList = factureRepository.findAll().collectList().block();
        assertThat(factureList).hasSize(databaseSizeBeforeCreate + 1);
        Facture testFacture = factureList.get(factureList.size() - 1);
        assertThat(testFacture.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testFacture.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    void createFactureWithExistingId() throws Exception {
        // Create the Facture with an existing ID
        facture.setId(1L);

        int databaseSizeBeforeCreate = factureRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facture))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Facture in the database
        List<Facture> factureList = factureRepository.findAll().collectList().block();
        assertThat(factureList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllFacturesAsStream() {
        // Initialize the database
        factureRepository.save(facture).block();

        List<Facture> factureList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Facture.class)
            .getResponseBody()
            .filter(facture::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(factureList).isNotNull();
        assertThat(factureList).hasSize(1);
        Facture testFacture = factureList.get(0);
        assertThat(testFacture.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testFacture.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    void getAllFactures() {
        // Initialize the database
        factureRepository.save(facture).block();

        // Get all the factureList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(facture.getId().intValue()))
            .jsonPath("$.[*].client")
            .value(hasItem(DEFAULT_CLIENT.intValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()));
    }

    @Test
    void getFacture() {
        // Initialize the database
        factureRepository.save(facture).block();

        // Get the facture
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, facture.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(facture.getId().intValue()))
            .jsonPath("$.client")
            .value(is(DEFAULT_CLIENT.intValue()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()));
    }

    @Test
    void getNonExistingFacture() {
        // Get the facture
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewFacture() throws Exception {
        // Initialize the database
        factureRepository.save(facture).block();

        int databaseSizeBeforeUpdate = factureRepository.findAll().collectList().block().size();

        // Update the facture
        Facture updatedFacture = factureRepository.findById(facture.getId()).block();
        updatedFacture.client(UPDATED_CLIENT).date(UPDATED_DATE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedFacture.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedFacture))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Facture in the database
        List<Facture> factureList = factureRepository.findAll().collectList().block();
        assertThat(factureList).hasSize(databaseSizeBeforeUpdate);
        Facture testFacture = factureList.get(factureList.size() - 1);
        assertThat(testFacture.getClient()).isEqualTo(UPDATED_CLIENT);
        assertThat(testFacture.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void putNonExistingFacture() throws Exception {
        int databaseSizeBeforeUpdate = factureRepository.findAll().collectList().block().size();
        facture.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, facture.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facture))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Facture in the database
        List<Facture> factureList = factureRepository.findAll().collectList().block();
        assertThat(factureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFacture() throws Exception {
        int databaseSizeBeforeUpdate = factureRepository.findAll().collectList().block().size();
        facture.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facture))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Facture in the database
        List<Facture> factureList = factureRepository.findAll().collectList().block();
        assertThat(factureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFacture() throws Exception {
        int databaseSizeBeforeUpdate = factureRepository.findAll().collectList().block().size();
        facture.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facture))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Facture in the database
        List<Facture> factureList = factureRepository.findAll().collectList().block();
        assertThat(factureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFactureWithPatch() throws Exception {
        // Initialize the database
        factureRepository.save(facture).block();

        int databaseSizeBeforeUpdate = factureRepository.findAll().collectList().block().size();

        // Update the facture using partial update
        Facture partialUpdatedFacture = new Facture();
        partialUpdatedFacture.setId(facture.getId());

        partialUpdatedFacture.date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFacture.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFacture))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Facture in the database
        List<Facture> factureList = factureRepository.findAll().collectList().block();
        assertThat(factureList).hasSize(databaseSizeBeforeUpdate);
        Facture testFacture = factureList.get(factureList.size() - 1);
        assertThat(testFacture.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testFacture.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void fullUpdateFactureWithPatch() throws Exception {
        // Initialize the database
        factureRepository.save(facture).block();

        int databaseSizeBeforeUpdate = factureRepository.findAll().collectList().block().size();

        // Update the facture using partial update
        Facture partialUpdatedFacture = new Facture();
        partialUpdatedFacture.setId(facture.getId());

        partialUpdatedFacture.client(UPDATED_CLIENT).date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFacture.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFacture))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Facture in the database
        List<Facture> factureList = factureRepository.findAll().collectList().block();
        assertThat(factureList).hasSize(databaseSizeBeforeUpdate);
        Facture testFacture = factureList.get(factureList.size() - 1);
        assertThat(testFacture.getClient()).isEqualTo(UPDATED_CLIENT);
        assertThat(testFacture.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void patchNonExistingFacture() throws Exception {
        int databaseSizeBeforeUpdate = factureRepository.findAll().collectList().block().size();
        facture.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, facture.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(facture))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Facture in the database
        List<Facture> factureList = factureRepository.findAll().collectList().block();
        assertThat(factureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFacture() throws Exception {
        int databaseSizeBeforeUpdate = factureRepository.findAll().collectList().block().size();
        facture.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(facture))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Facture in the database
        List<Facture> factureList = factureRepository.findAll().collectList().block();
        assertThat(factureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFacture() throws Exception {
        int databaseSizeBeforeUpdate = factureRepository.findAll().collectList().block().size();
        facture.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(facture))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Facture in the database
        List<Facture> factureList = factureRepository.findAll().collectList().block();
        assertThat(factureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFacture() {
        // Initialize the database
        factureRepository.save(facture).block();

        int databaseSizeBeforeDelete = factureRepository.findAll().collectList().block().size();

        // Delete the facture
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, facture.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Facture> factureList = factureRepository.findAll().collectList().block();
        assertThat(factureList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
