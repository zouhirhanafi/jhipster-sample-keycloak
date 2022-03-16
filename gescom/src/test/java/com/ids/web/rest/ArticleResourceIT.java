package com.ids.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.ids.IntegrationTest;
import com.ids.domain.Article;
import com.ids.repository.ArticleRepository;
import com.ids.repository.EntityManager;
import java.time.Duration;
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
 * Integration tests for the {@link ArticleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ArticleResourceIT {

    private static final String DEFAULT_DESIGNATION = "AAAAAAAAAA";
    private static final String UPDATED_DESIGNATION = "BBBBBBBBBB";

    private static final Double DEFAULT_PU = 1D;
    private static final Double UPDATED_PU = 2D;

    private static final String ENTITY_API_URL = "/api/articles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Article article;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Article createEntity(EntityManager em) {
        Article article = new Article().designation(DEFAULT_DESIGNATION).pu(DEFAULT_PU);
        return article;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Article createUpdatedEntity(EntityManager em) {
        Article article = new Article().designation(UPDATED_DESIGNATION).pu(UPDATED_PU);
        return article;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Article.class).block();
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
        article = createEntity(em);
    }

    @Test
    void createArticle() throws Exception {
        int databaseSizeBeforeCreate = articleRepository.findAll().collectList().block().size();
        // Create the Article
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(article))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll().collectList().block();
        assertThat(articleList).hasSize(databaseSizeBeforeCreate + 1);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getDesignation()).isEqualTo(DEFAULT_DESIGNATION);
        assertThat(testArticle.getPu()).isEqualTo(DEFAULT_PU);
    }

    @Test
    void createArticleWithExistingId() throws Exception {
        // Create the Article with an existing ID
        article.setId(1L);

        int databaseSizeBeforeCreate = articleRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(article))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll().collectList().block();
        assertThat(articleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllArticlesAsStream() {
        // Initialize the database
        articleRepository.save(article).block();

        List<Article> articleList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Article.class)
            .getResponseBody()
            .filter(article::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(articleList).isNotNull();
        assertThat(articleList).hasSize(1);
        Article testArticle = articleList.get(0);
        assertThat(testArticle.getDesignation()).isEqualTo(DEFAULT_DESIGNATION);
        assertThat(testArticle.getPu()).isEqualTo(DEFAULT_PU);
    }

    @Test
    void getAllArticles() {
        // Initialize the database
        articleRepository.save(article).block();

        // Get all the articleList
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
            .value(hasItem(article.getId().intValue()))
            .jsonPath("$.[*].designation")
            .value(hasItem(DEFAULT_DESIGNATION))
            .jsonPath("$.[*].pu")
            .value(hasItem(DEFAULT_PU.doubleValue()));
    }

    @Test
    void getArticle() {
        // Initialize the database
        articleRepository.save(article).block();

        // Get the article
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, article.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(article.getId().intValue()))
            .jsonPath("$.designation")
            .value(is(DEFAULT_DESIGNATION))
            .jsonPath("$.pu")
            .value(is(DEFAULT_PU.doubleValue()));
    }

    @Test
    void getNonExistingArticle() {
        // Get the article
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewArticle() throws Exception {
        // Initialize the database
        articleRepository.save(article).block();

        int databaseSizeBeforeUpdate = articleRepository.findAll().collectList().block().size();

        // Update the article
        Article updatedArticle = articleRepository.findById(article.getId()).block();
        updatedArticle.designation(UPDATED_DESIGNATION).pu(UPDATED_PU);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedArticle.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedArticle))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll().collectList().block();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getDesignation()).isEqualTo(UPDATED_DESIGNATION);
        assertThat(testArticle.getPu()).isEqualTo(UPDATED_PU);
    }

    @Test
    void putNonExistingArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().collectList().block().size();
        article.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, article.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(article))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll().collectList().block();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().collectList().block().size();
        article.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(article))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll().collectList().block();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().collectList().block().size();
        article.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(article))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll().collectList().block();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateArticleWithPatch() throws Exception {
        // Initialize the database
        articleRepository.save(article).block();

        int databaseSizeBeforeUpdate = articleRepository.findAll().collectList().block().size();

        // Update the article using partial update
        Article partialUpdatedArticle = new Article();
        partialUpdatedArticle.setId(article.getId());

        partialUpdatedArticle.designation(UPDATED_DESIGNATION).pu(UPDATED_PU);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedArticle.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedArticle))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll().collectList().block();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getDesignation()).isEqualTo(UPDATED_DESIGNATION);
        assertThat(testArticle.getPu()).isEqualTo(UPDATED_PU);
    }

    @Test
    void fullUpdateArticleWithPatch() throws Exception {
        // Initialize the database
        articleRepository.save(article).block();

        int databaseSizeBeforeUpdate = articleRepository.findAll().collectList().block().size();

        // Update the article using partial update
        Article partialUpdatedArticle = new Article();
        partialUpdatedArticle.setId(article.getId());

        partialUpdatedArticle.designation(UPDATED_DESIGNATION).pu(UPDATED_PU);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedArticle.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedArticle))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll().collectList().block();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getDesignation()).isEqualTo(UPDATED_DESIGNATION);
        assertThat(testArticle.getPu()).isEqualTo(UPDATED_PU);
    }

    @Test
    void patchNonExistingArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().collectList().block().size();
        article.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, article.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(article))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll().collectList().block();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().collectList().block().size();
        article.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(article))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll().collectList().block();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().collectList().block().size();
        article.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(article))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll().collectList().block();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteArticle() {
        // Initialize the database
        articleRepository.save(article).block();

        int databaseSizeBeforeDelete = articleRepository.findAll().collectList().block().size();

        // Delete the article
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, article.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Article> articleList = articleRepository.findAll().collectList().block();
        assertThat(articleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
