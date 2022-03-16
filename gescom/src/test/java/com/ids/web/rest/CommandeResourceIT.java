package com.ids.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.ids.IntegrationTest;
import com.ids.domain.Commande;
import com.ids.repository.CommandeRepository;
import com.ids.repository.EntityManager;
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
 * Integration tests for the {@link CommandeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CommandeResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_CLIENT = 1L;
    private static final Long UPDATED_CLIENT = 2L;

    private static final String ENTITY_API_URL = "/api/commandes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Commande commande;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commande createEntity(EntityManager em) {
        Commande commande = new Commande().date(DEFAULT_DATE).client(DEFAULT_CLIENT);
        return commande;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commande createUpdatedEntity(EntityManager em) {
        Commande commande = new Commande().date(UPDATED_DATE).client(UPDATED_CLIENT);
        return commande;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Commande.class).block();
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
        commande = createEntity(em);
    }

    @Test
    void createCommande() throws Exception {
        int databaseSizeBeforeCreate = commandeRepository.findAll().collectList().block().size();
        // Create the Commande
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeCreate + 1);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
        assertThat(testCommande.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testCommande.getClient()).isEqualTo(DEFAULT_CLIENT);
    }

    @Test
    void createCommandeWithExistingId() throws Exception {
        // Create the Commande with an existing ID
        commande.setId(1L);

        int databaseSizeBeforeCreate = commandeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCommandesAsStream() {
        // Initialize the database
        commandeRepository.save(commande).block();

        List<Commande> commandeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Commande.class)
            .getResponseBody()
            .filter(commande::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(commandeList).isNotNull();
        assertThat(commandeList).hasSize(1);
        Commande testCommande = commandeList.get(0);
        assertThat(testCommande.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testCommande.getClient()).isEqualTo(DEFAULT_CLIENT);
    }

    @Test
    void getAllCommandes() {
        // Initialize the database
        commandeRepository.save(commande).block();

        // Get all the commandeList
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
            .value(hasItem(commande.getId().intValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].client")
            .value(hasItem(DEFAULT_CLIENT.intValue()));
    }

    @Test
    void getCommande() {
        // Initialize the database
        commandeRepository.save(commande).block();

        // Get the commande
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, commande.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(commande.getId().intValue()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.client")
            .value(is(DEFAULT_CLIENT.intValue()));
    }

    @Test
    void getNonExistingCommande() {
        // Get the commande
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCommande() throws Exception {
        // Initialize the database
        commandeRepository.save(commande).block();

        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();

        // Update the commande
        Commande updatedCommande = commandeRepository.findById(commande.getId()).block();
        updatedCommande.date(UPDATED_DATE).client(UPDATED_CLIENT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCommande.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCommande))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
        assertThat(testCommande.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testCommande.getClient()).isEqualTo(UPDATED_CLIENT);
    }

    @Test
    void putNonExistingCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();
        commande.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, commande.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();
        commande.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();
        commande.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCommandeWithPatch() throws Exception {
        // Initialize the database
        commandeRepository.save(commande).block();

        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();

        // Update the commande using partial update
        Commande partialUpdatedCommande = new Commande();
        partialUpdatedCommande.setId(commande.getId());

        partialUpdatedCommande.date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCommande.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCommande))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
        assertThat(testCommande.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testCommande.getClient()).isEqualTo(DEFAULT_CLIENT);
    }

    @Test
    void fullUpdateCommandeWithPatch() throws Exception {
        // Initialize the database
        commandeRepository.save(commande).block();

        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();

        // Update the commande using partial update
        Commande partialUpdatedCommande = new Commande();
        partialUpdatedCommande.setId(commande.getId());

        partialUpdatedCommande.date(UPDATED_DATE).client(UPDATED_CLIENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCommande.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCommande))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
        assertThat(testCommande.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testCommande.getClient()).isEqualTo(UPDATED_CLIENT);
    }

    @Test
    void patchNonExistingCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();
        commande.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, commande.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();
        commande.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();
        commande.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCommande() {
        // Initialize the database
        commandeRepository.save(commande).block();

        int databaseSizeBeforeDelete = commandeRepository.findAll().collectList().block().size();

        // Delete the commande
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, commande.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
