package com.ids.web.rest;

import static com.ids.domain.ConteneurAsserts.*;
import static com.ids.web.rest.TestUtil.createUpdateProxyForBean;
import static com.ids.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ids.IntegrationTest;
import com.ids.domain.Conteneur;
import com.ids.repository.ConteneurRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ConteneurResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ConteneurResourceIT {

    private static final Integer DEFAULT_STATUT = 1;
    private static final Integer UPDATED_STATUT = 2;
    private static final Integer SMALLER_STATUT = 1 - 1;

    private static final Instant DEFAULT_DATE_ENTREE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_ENTREE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final ZonedDateTime DEFAULT_DATE_SORTIE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_SORTIE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE_SORTIE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Integer DEFAULT_ZONE = 1;
    private static final Integer UPDATED_ZONE = 2;
    private static final Integer SMALLER_ZONE = 1 - 1;

    private static final Integer DEFAULT_LIGNE = 1;
    private static final Integer UPDATED_LIGNE = 2;
    private static final Integer SMALLER_LIGNE = 1 - 1;

    private static final Integer DEFAULT_COLONNE = 1;
    private static final Integer UPDATED_COLONNE = 2;
    private static final Integer SMALLER_COLONNE = 1 - 1;

    private static final String DEFAULT_COMMENTAIRE = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/conteneurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ConteneurRepository conteneurRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restConteneurMockMvc;

    private Conteneur conteneur;

    private Conteneur insertedConteneur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Conteneur createEntity() {
        return new Conteneur()
            .statut(DEFAULT_STATUT)
            .dateEntree(DEFAULT_DATE_ENTREE)
            .dateSortie(DEFAULT_DATE_SORTIE)
            .zone(DEFAULT_ZONE)
            .ligne(DEFAULT_LIGNE)
            .colonne(DEFAULT_COLONNE)
            .commentaire(DEFAULT_COMMENTAIRE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Conteneur createUpdatedEntity() {
        return new Conteneur()
            .statut(UPDATED_STATUT)
            .dateEntree(UPDATED_DATE_ENTREE)
            .dateSortie(UPDATED_DATE_SORTIE)
            .zone(UPDATED_ZONE)
            .ligne(UPDATED_LIGNE)
            .colonne(UPDATED_COLONNE)
            .commentaire(UPDATED_COMMENTAIRE);
    }

    @BeforeEach
    public void initTest() {
        conteneur = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedConteneur != null) {
            conteneurRepository.delete(insertedConteneur);
            insertedConteneur = null;
        }
    }

    @Test
    @Transactional
    void createConteneur() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Conteneur
        var returnedConteneur = om.readValue(
            restConteneurMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conteneur)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Conteneur.class
        );

        // Validate the Conteneur in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertConteneurUpdatableFieldsEquals(returnedConteneur, getPersistedConteneur(returnedConteneur));

        insertedConteneur = returnedConteneur;
    }

    @Test
    @Transactional
    void createConteneurWithExistingId() throws Exception {
        // Create the Conteneur with an existing ID
        conteneur.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restConteneurMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conteneur)))
            .andExpect(status().isBadRequest());

        // Validate the Conteneur in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllConteneurs() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList
        restConteneurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(conteneur.getId().intValue())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT)))
            .andExpect(jsonPath("$.[*].dateEntree").value(hasItem(DEFAULT_DATE_ENTREE.toString())))
            .andExpect(jsonPath("$.[*].dateSortie").value(hasItem(sameInstant(DEFAULT_DATE_SORTIE))))
            .andExpect(jsonPath("$.[*].zone").value(hasItem(DEFAULT_ZONE)))
            .andExpect(jsonPath("$.[*].ligne").value(hasItem(DEFAULT_LIGNE)))
            .andExpect(jsonPath("$.[*].colonne").value(hasItem(DEFAULT_COLONNE)))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));
    }

    @Test
    @Transactional
    void getConteneur() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get the conteneur
        restConteneurMockMvc
            .perform(get(ENTITY_API_URL_ID, conteneur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(conteneur.getId().intValue()))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT))
            .andExpect(jsonPath("$.dateEntree").value(DEFAULT_DATE_ENTREE.toString()))
            .andExpect(jsonPath("$.dateSortie").value(sameInstant(DEFAULT_DATE_SORTIE)))
            .andExpect(jsonPath("$.zone").value(DEFAULT_ZONE))
            .andExpect(jsonPath("$.ligne").value(DEFAULT_LIGNE))
            .andExpect(jsonPath("$.colonne").value(DEFAULT_COLONNE))
            .andExpect(jsonPath("$.commentaire").value(DEFAULT_COMMENTAIRE));
    }

    @Test
    @Transactional
    void getConteneursByIdFiltering() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        Long id = conteneur.getId();

        defaultConteneurFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultConteneurFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultConteneurFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllConteneursByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where statut equals to
        defaultConteneurFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllConteneursByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where statut in
        defaultConteneurFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllConteneursByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where statut is not null
        defaultConteneurFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllConteneursByStatutIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where statut is greater than or equal to
        defaultConteneurFiltering("statut.greaterThanOrEqual=" + DEFAULT_STATUT, "statut.greaterThanOrEqual=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllConteneursByStatutIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where statut is less than or equal to
        defaultConteneurFiltering("statut.lessThanOrEqual=" + DEFAULT_STATUT, "statut.lessThanOrEqual=" + SMALLER_STATUT);
    }

    @Test
    @Transactional
    void getAllConteneursByStatutIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where statut is less than
        defaultConteneurFiltering("statut.lessThan=" + UPDATED_STATUT, "statut.lessThan=" + DEFAULT_STATUT);
    }

    @Test
    @Transactional
    void getAllConteneursByStatutIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where statut is greater than
        defaultConteneurFiltering("statut.greaterThan=" + SMALLER_STATUT, "statut.greaterThan=" + DEFAULT_STATUT);
    }

    @Test
    @Transactional
    void getAllConteneursByDateEntreeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where dateEntree equals to
        defaultConteneurFiltering("dateEntree.equals=" + DEFAULT_DATE_ENTREE, "dateEntree.equals=" + UPDATED_DATE_ENTREE);
    }

    @Test
    @Transactional
    void getAllConteneursByDateEntreeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where dateEntree in
        defaultConteneurFiltering(
            "dateEntree.in=" + DEFAULT_DATE_ENTREE + "," + UPDATED_DATE_ENTREE,
            "dateEntree.in=" + UPDATED_DATE_ENTREE
        );
    }

    @Test
    @Transactional
    void getAllConteneursByDateEntreeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where dateEntree is not null
        defaultConteneurFiltering("dateEntree.specified=true", "dateEntree.specified=false");
    }

    @Test
    @Transactional
    void getAllConteneursByDateSortieIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where dateSortie equals to
        defaultConteneurFiltering("dateSortie.equals=" + DEFAULT_DATE_SORTIE, "dateSortie.equals=" + UPDATED_DATE_SORTIE);
    }

    @Test
    @Transactional
    void getAllConteneursByDateSortieIsInShouldWork() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where dateSortie in
        defaultConteneurFiltering(
            "dateSortie.in=" + DEFAULT_DATE_SORTIE + "," + UPDATED_DATE_SORTIE,
            "dateSortie.in=" + UPDATED_DATE_SORTIE
        );
    }

    @Test
    @Transactional
    void getAllConteneursByDateSortieIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where dateSortie is not null
        defaultConteneurFiltering("dateSortie.specified=true", "dateSortie.specified=false");
    }

    @Test
    @Transactional
    void getAllConteneursByDateSortieIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where dateSortie is greater than or equal to
        defaultConteneurFiltering(
            "dateSortie.greaterThanOrEqual=" + DEFAULT_DATE_SORTIE,
            "dateSortie.greaterThanOrEqual=" + UPDATED_DATE_SORTIE
        );
    }

    @Test
    @Transactional
    void getAllConteneursByDateSortieIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where dateSortie is less than or equal to
        defaultConteneurFiltering("dateSortie.lessThanOrEqual=" + DEFAULT_DATE_SORTIE, "dateSortie.lessThanOrEqual=" + SMALLER_DATE_SORTIE);
    }

    @Test
    @Transactional
    void getAllConteneursByDateSortieIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where dateSortie is less than
        defaultConteneurFiltering("dateSortie.lessThan=" + UPDATED_DATE_SORTIE, "dateSortie.lessThan=" + DEFAULT_DATE_SORTIE);
    }

    @Test
    @Transactional
    void getAllConteneursByDateSortieIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where dateSortie is greater than
        defaultConteneurFiltering("dateSortie.greaterThan=" + SMALLER_DATE_SORTIE, "dateSortie.greaterThan=" + DEFAULT_DATE_SORTIE);
    }

    @Test
    @Transactional
    void getAllConteneursByZoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where zone equals to
        defaultConteneurFiltering("zone.equals=" + DEFAULT_ZONE, "zone.equals=" + UPDATED_ZONE);
    }

    @Test
    @Transactional
    void getAllConteneursByZoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where zone in
        defaultConteneurFiltering("zone.in=" + DEFAULT_ZONE + "," + UPDATED_ZONE, "zone.in=" + UPDATED_ZONE);
    }

    @Test
    @Transactional
    void getAllConteneursByZoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where zone is not null
        defaultConteneurFiltering("zone.specified=true", "zone.specified=false");
    }

    @Test
    @Transactional
    void getAllConteneursByZoneIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where zone is greater than or equal to
        defaultConteneurFiltering("zone.greaterThanOrEqual=" + DEFAULT_ZONE, "zone.greaterThanOrEqual=" + UPDATED_ZONE);
    }

    @Test
    @Transactional
    void getAllConteneursByZoneIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where zone is less than or equal to
        defaultConteneurFiltering("zone.lessThanOrEqual=" + DEFAULT_ZONE, "zone.lessThanOrEqual=" + SMALLER_ZONE);
    }

    @Test
    @Transactional
    void getAllConteneursByZoneIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where zone is less than
        defaultConteneurFiltering("zone.lessThan=" + UPDATED_ZONE, "zone.lessThan=" + DEFAULT_ZONE);
    }

    @Test
    @Transactional
    void getAllConteneursByZoneIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where zone is greater than
        defaultConteneurFiltering("zone.greaterThan=" + SMALLER_ZONE, "zone.greaterThan=" + DEFAULT_ZONE);
    }

    @Test
    @Transactional
    void getAllConteneursByLigneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where ligne equals to
        defaultConteneurFiltering("ligne.equals=" + DEFAULT_LIGNE, "ligne.equals=" + UPDATED_LIGNE);
    }

    @Test
    @Transactional
    void getAllConteneursByLigneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where ligne in
        defaultConteneurFiltering("ligne.in=" + DEFAULT_LIGNE + "," + UPDATED_LIGNE, "ligne.in=" + UPDATED_LIGNE);
    }

    @Test
    @Transactional
    void getAllConteneursByLigneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where ligne is not null
        defaultConteneurFiltering("ligne.specified=true", "ligne.specified=false");
    }

    @Test
    @Transactional
    void getAllConteneursByLigneIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where ligne is greater than or equal to
        defaultConteneurFiltering("ligne.greaterThanOrEqual=" + DEFAULT_LIGNE, "ligne.greaterThanOrEqual=" + UPDATED_LIGNE);
    }

    @Test
    @Transactional
    void getAllConteneursByLigneIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where ligne is less than or equal to
        defaultConteneurFiltering("ligne.lessThanOrEqual=" + DEFAULT_LIGNE, "ligne.lessThanOrEqual=" + SMALLER_LIGNE);
    }

    @Test
    @Transactional
    void getAllConteneursByLigneIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where ligne is less than
        defaultConteneurFiltering("ligne.lessThan=" + UPDATED_LIGNE, "ligne.lessThan=" + DEFAULT_LIGNE);
    }

    @Test
    @Transactional
    void getAllConteneursByLigneIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where ligne is greater than
        defaultConteneurFiltering("ligne.greaterThan=" + SMALLER_LIGNE, "ligne.greaterThan=" + DEFAULT_LIGNE);
    }

    @Test
    @Transactional
    void getAllConteneursByColonneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where colonne equals to
        defaultConteneurFiltering("colonne.equals=" + DEFAULT_COLONNE, "colonne.equals=" + UPDATED_COLONNE);
    }

    @Test
    @Transactional
    void getAllConteneursByColonneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where colonne in
        defaultConteneurFiltering("colonne.in=" + DEFAULT_COLONNE + "," + UPDATED_COLONNE, "colonne.in=" + UPDATED_COLONNE);
    }

    @Test
    @Transactional
    void getAllConteneursByColonneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where colonne is not null
        defaultConteneurFiltering("colonne.specified=true", "colonne.specified=false");
    }

    @Test
    @Transactional
    void getAllConteneursByColonneIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where colonne is greater than or equal to
        defaultConteneurFiltering("colonne.greaterThanOrEqual=" + DEFAULT_COLONNE, "colonne.greaterThanOrEqual=" + UPDATED_COLONNE);
    }

    @Test
    @Transactional
    void getAllConteneursByColonneIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where colonne is less than or equal to
        defaultConteneurFiltering("colonne.lessThanOrEqual=" + DEFAULT_COLONNE, "colonne.lessThanOrEqual=" + SMALLER_COLONNE);
    }

    @Test
    @Transactional
    void getAllConteneursByColonneIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where colonne is less than
        defaultConteneurFiltering("colonne.lessThan=" + UPDATED_COLONNE, "colonne.lessThan=" + DEFAULT_COLONNE);
    }

    @Test
    @Transactional
    void getAllConteneursByColonneIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where colonne is greater than
        defaultConteneurFiltering("colonne.greaterThan=" + SMALLER_COLONNE, "colonne.greaterThan=" + DEFAULT_COLONNE);
    }

    @Test
    @Transactional
    void getAllConteneursByCommentaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where commentaire equals to
        defaultConteneurFiltering("commentaire.equals=" + DEFAULT_COMMENTAIRE, "commentaire.equals=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllConteneursByCommentaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where commentaire in
        defaultConteneurFiltering(
            "commentaire.in=" + DEFAULT_COMMENTAIRE + "," + UPDATED_COMMENTAIRE,
            "commentaire.in=" + UPDATED_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllConteneursByCommentaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where commentaire is not null
        defaultConteneurFiltering("commentaire.specified=true", "commentaire.specified=false");
    }

    @Test
    @Transactional
    void getAllConteneursByCommentaireContainsSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where commentaire contains
        defaultConteneurFiltering("commentaire.contains=" + DEFAULT_COMMENTAIRE, "commentaire.contains=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllConteneursByCommentaireNotContainsSomething() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        // Get all the conteneurList where commentaire does not contain
        defaultConteneurFiltering("commentaire.doesNotContain=" + UPDATED_COMMENTAIRE, "commentaire.doesNotContain=" + DEFAULT_COMMENTAIRE);
    }

    private void defaultConteneurFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultConteneurShouldBeFound(shouldBeFound);
        defaultConteneurShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultConteneurShouldBeFound(String filter) throws Exception {
        restConteneurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(conteneur.getId().intValue())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT)))
            .andExpect(jsonPath("$.[*].dateEntree").value(hasItem(DEFAULT_DATE_ENTREE.toString())))
            .andExpect(jsonPath("$.[*].dateSortie").value(hasItem(sameInstant(DEFAULT_DATE_SORTIE))))
            .andExpect(jsonPath("$.[*].zone").value(hasItem(DEFAULT_ZONE)))
            .andExpect(jsonPath("$.[*].ligne").value(hasItem(DEFAULT_LIGNE)))
            .andExpect(jsonPath("$.[*].colonne").value(hasItem(DEFAULT_COLONNE)))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));

        // Check, that the count call also returns 1
        restConteneurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultConteneurShouldNotBeFound(String filter) throws Exception {
        restConteneurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restConteneurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingConteneur() throws Exception {
        // Get the conteneur
        restConteneurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingConteneur() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the conteneur
        Conteneur updatedConteneur = conteneurRepository.findById(conteneur.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedConteneur are not directly saved in db
        em.detach(updatedConteneur);
        updatedConteneur
            .statut(UPDATED_STATUT)
            .dateEntree(UPDATED_DATE_ENTREE)
            .dateSortie(UPDATED_DATE_SORTIE)
            .zone(UPDATED_ZONE)
            .ligne(UPDATED_LIGNE)
            .colonne(UPDATED_COLONNE)
            .commentaire(UPDATED_COMMENTAIRE);

        restConteneurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedConteneur.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedConteneur))
            )
            .andExpect(status().isOk());

        // Validate the Conteneur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedConteneurToMatchAllProperties(updatedConteneur);
    }

    @Test
    @Transactional
    void putNonExistingConteneur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conteneur.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConteneurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, conteneur.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(conteneur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conteneur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchConteneur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conteneur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConteneurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(conteneur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conteneur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamConteneur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conteneur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConteneurMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conteneur)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Conteneur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateConteneurWithPatch() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the conteneur using partial update
        Conteneur partialUpdatedConteneur = new Conteneur();
        partialUpdatedConteneur.setId(conteneur.getId());

        partialUpdatedConteneur
            .statut(UPDATED_STATUT)
            .dateEntree(UPDATED_DATE_ENTREE)
            .dateSortie(UPDATED_DATE_SORTIE)
            .colonne(UPDATED_COLONNE);

        restConteneurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConteneur.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConteneur))
            )
            .andExpect(status().isOk());

        // Validate the Conteneur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConteneurUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedConteneur, conteneur),
            getPersistedConteneur(conteneur)
        );
    }

    @Test
    @Transactional
    void fullUpdateConteneurWithPatch() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the conteneur using partial update
        Conteneur partialUpdatedConteneur = new Conteneur();
        partialUpdatedConteneur.setId(conteneur.getId());

        partialUpdatedConteneur
            .statut(UPDATED_STATUT)
            .dateEntree(UPDATED_DATE_ENTREE)
            .dateSortie(UPDATED_DATE_SORTIE)
            .zone(UPDATED_ZONE)
            .ligne(UPDATED_LIGNE)
            .colonne(UPDATED_COLONNE)
            .commentaire(UPDATED_COMMENTAIRE);

        restConteneurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConteneur.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConteneur))
            )
            .andExpect(status().isOk());

        // Validate the Conteneur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConteneurUpdatableFieldsEquals(partialUpdatedConteneur, getPersistedConteneur(partialUpdatedConteneur));
    }

    @Test
    @Transactional
    void patchNonExistingConteneur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conteneur.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConteneurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, conteneur.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(conteneur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conteneur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchConteneur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conteneur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConteneurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(conteneur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conteneur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamConteneur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conteneur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConteneurMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(conteneur))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Conteneur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteConteneur() throws Exception {
        // Initialize the database
        insertedConteneur = conteneurRepository.saveAndFlush(conteneur);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the conteneur
        restConteneurMockMvc
            .perform(delete(ENTITY_API_URL_ID, conteneur.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return conteneurRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Conteneur getPersistedConteneur(Conteneur conteneur) {
        return conteneurRepository.findById(conteneur.getId()).orElseThrow();
    }

    protected void assertPersistedConteneurToMatchAllProperties(Conteneur expectedConteneur) {
        assertConteneurAllPropertiesEquals(expectedConteneur, getPersistedConteneur(expectedConteneur));
    }

    protected void assertPersistedConteneurToMatchUpdatableProperties(Conteneur expectedConteneur) {
        assertConteneurAllUpdatablePropertiesEquals(expectedConteneur, getPersistedConteneur(expectedConteneur));
    }
}
