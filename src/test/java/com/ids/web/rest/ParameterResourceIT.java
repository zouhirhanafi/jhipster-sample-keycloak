package com.ids.web.rest;

import static com.ids.domain.ParameterAsserts.*;
import static com.ids.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ids.IntegrationTest;
import com.ids.domain.Parameter;
import com.ids.domain.Parameter;
import com.ids.repository.ParameterRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ParameterResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParameterResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVATED = false;
    private static final Boolean UPDATED_ACTIVATED = true;

    private static final String DEFAULT_LIB_2 = "AAAAAAAAAA";
    private static final String UPDATED_LIB_2 = "BBBBBBBBBB";

    private static final String DEFAULT_LIB_3 = "AAAAAAAAAA";
    private static final String UPDATED_LIB_3 = "BBBBBBBBBB";

    private static final String DEFAULT_REF_EXTERNE = "AAAAAAAAAA";
    private static final String UPDATED_REF_EXTERNE = "BBBBBBBBBB";

    private static final String DEFAULT_VAL_1 = "AAAAAAAAAA";
    private static final String UPDATED_VAL_1 = "BBBBBBBBBB";

    private static final String DEFAULT_VAL_2 = "AAAAAAAAAA";
    private static final String UPDATED_VAL_2 = "BBBBBBBBBB";

    private static final String DEFAULT_VAL_3 = "AAAAAAAAAA";
    private static final String UPDATED_VAL_3 = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDRE = 1;
    private static final Integer UPDATED_ORDRE = 2;
    private static final Integer SMALLER_ORDRE = 1 - 1;

    private static final String ENTITY_API_URL = "/api/parameters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParameterRepository parameterRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParameterMockMvc;

    private Parameter parameter;

    private Parameter insertedParameter;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parameter createEntity() {
        return new Parameter()
            .label(DEFAULT_LABEL)
            .activated(DEFAULT_ACTIVATED)
            .lib2(DEFAULT_LIB_2)
            .lib3(DEFAULT_LIB_3)
            .refExterne(DEFAULT_REF_EXTERNE)
            .val1(DEFAULT_VAL_1)
            .val2(DEFAULT_VAL_2)
            .val3(DEFAULT_VAL_3)
            .ordre(DEFAULT_ORDRE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parameter createUpdatedEntity() {
        return new Parameter()
            .label(UPDATED_LABEL)
            .activated(UPDATED_ACTIVATED)
            .lib2(UPDATED_LIB_2)
            .lib3(UPDATED_LIB_3)
            .refExterne(UPDATED_REF_EXTERNE)
            .val1(UPDATED_VAL_1)
            .val2(UPDATED_VAL_2)
            .val3(UPDATED_VAL_3)
            .ordre(UPDATED_ORDRE);
    }

    @BeforeEach
    public void initTest() {
        parameter = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedParameter != null) {
            parameterRepository.delete(insertedParameter);
            insertedParameter = null;
        }
    }

    @Test
    @Transactional
    void createParameter() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Parameter
        var returnedParameter = om.readValue(
            restParameterMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parameter)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Parameter.class
        );

        // Validate the Parameter in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertParameterUpdatableFieldsEquals(returnedParameter, getPersistedParameter(returnedParameter));

        insertedParameter = returnedParameter;
    }

    @Test
    @Transactional
    void createParameterWithExistingId() throws Exception {
        // Create the Parameter with an existing ID
        parameter.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParameterMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parameter)))
            .andExpect(status().isBadRequest());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllParameters() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList
        restParameterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parameter.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].lib2").value(hasItem(DEFAULT_LIB_2)))
            .andExpect(jsonPath("$.[*].lib3").value(hasItem(DEFAULT_LIB_3)))
            .andExpect(jsonPath("$.[*].refExterne").value(hasItem(DEFAULT_REF_EXTERNE)))
            .andExpect(jsonPath("$.[*].val1").value(hasItem(DEFAULT_VAL_1)))
            .andExpect(jsonPath("$.[*].val2").value(hasItem(DEFAULT_VAL_2)))
            .andExpect(jsonPath("$.[*].val3").value(hasItem(DEFAULT_VAL_3)))
            .andExpect(jsonPath("$.[*].ordre").value(hasItem(DEFAULT_ORDRE)));
    }

    @Test
    @Transactional
    void getParameter() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get the parameter
        restParameterMockMvc
            .perform(get(ENTITY_API_URL_ID, parameter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(parameter.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.activated").value(DEFAULT_ACTIVATED.booleanValue()))
            .andExpect(jsonPath("$.lib2").value(DEFAULT_LIB_2))
            .andExpect(jsonPath("$.lib3").value(DEFAULT_LIB_3))
            .andExpect(jsonPath("$.refExterne").value(DEFAULT_REF_EXTERNE))
            .andExpect(jsonPath("$.val1").value(DEFAULT_VAL_1))
            .andExpect(jsonPath("$.val2").value(DEFAULT_VAL_2))
            .andExpect(jsonPath("$.val3").value(DEFAULT_VAL_3))
            .andExpect(jsonPath("$.ordre").value(DEFAULT_ORDRE));
    }

    @Test
    @Transactional
    void getParametersByIdFiltering() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        Long id = parameter.getId();

        defaultParameterFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultParameterFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultParameterFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllParametersByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where label equals to
        defaultParameterFiltering("label.equals=" + DEFAULT_LABEL, "label.equals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllParametersByLabelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where label in
        defaultParameterFiltering("label.in=" + DEFAULT_LABEL + "," + UPDATED_LABEL, "label.in=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllParametersByLabelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where label is not null
        defaultParameterFiltering("label.specified=true", "label.specified=false");
    }

    @Test
    @Transactional
    void getAllParametersByLabelContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where label contains
        defaultParameterFiltering("label.contains=" + DEFAULT_LABEL, "label.contains=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllParametersByLabelNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where label does not contain
        defaultParameterFiltering("label.doesNotContain=" + UPDATED_LABEL, "label.doesNotContain=" + DEFAULT_LABEL);
    }

    @Test
    @Transactional
    void getAllParametersByActivatedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where activated equals to
        defaultParameterFiltering("activated.equals=" + DEFAULT_ACTIVATED, "activated.equals=" + UPDATED_ACTIVATED);
    }

    @Test
    @Transactional
    void getAllParametersByActivatedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where activated in
        defaultParameterFiltering("activated.in=" + DEFAULT_ACTIVATED + "," + UPDATED_ACTIVATED, "activated.in=" + UPDATED_ACTIVATED);
    }

    @Test
    @Transactional
    void getAllParametersByActivatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where activated is not null
        defaultParameterFiltering("activated.specified=true", "activated.specified=false");
    }

    @Test
    @Transactional
    void getAllParametersByLib2IsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where lib2 equals to
        defaultParameterFiltering("lib2.equals=" + DEFAULT_LIB_2, "lib2.equals=" + UPDATED_LIB_2);
    }

    @Test
    @Transactional
    void getAllParametersByLib2IsInShouldWork() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where lib2 in
        defaultParameterFiltering("lib2.in=" + DEFAULT_LIB_2 + "," + UPDATED_LIB_2, "lib2.in=" + UPDATED_LIB_2);
    }

    @Test
    @Transactional
    void getAllParametersByLib2IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where lib2 is not null
        defaultParameterFiltering("lib2.specified=true", "lib2.specified=false");
    }

    @Test
    @Transactional
    void getAllParametersByLib2ContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where lib2 contains
        defaultParameterFiltering("lib2.contains=" + DEFAULT_LIB_2, "lib2.contains=" + UPDATED_LIB_2);
    }

    @Test
    @Transactional
    void getAllParametersByLib2NotContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where lib2 does not contain
        defaultParameterFiltering("lib2.doesNotContain=" + UPDATED_LIB_2, "lib2.doesNotContain=" + DEFAULT_LIB_2);
    }

    @Test
    @Transactional
    void getAllParametersByLib3IsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where lib3 equals to
        defaultParameterFiltering("lib3.equals=" + DEFAULT_LIB_3, "lib3.equals=" + UPDATED_LIB_3);
    }

    @Test
    @Transactional
    void getAllParametersByLib3IsInShouldWork() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where lib3 in
        defaultParameterFiltering("lib3.in=" + DEFAULT_LIB_3 + "," + UPDATED_LIB_3, "lib3.in=" + UPDATED_LIB_3);
    }

    @Test
    @Transactional
    void getAllParametersByLib3IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where lib3 is not null
        defaultParameterFiltering("lib3.specified=true", "lib3.specified=false");
    }

    @Test
    @Transactional
    void getAllParametersByLib3ContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where lib3 contains
        defaultParameterFiltering("lib3.contains=" + DEFAULT_LIB_3, "lib3.contains=" + UPDATED_LIB_3);
    }

    @Test
    @Transactional
    void getAllParametersByLib3NotContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where lib3 does not contain
        defaultParameterFiltering("lib3.doesNotContain=" + UPDATED_LIB_3, "lib3.doesNotContain=" + DEFAULT_LIB_3);
    }

    @Test
    @Transactional
    void getAllParametersByRefExterneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where refExterne equals to
        defaultParameterFiltering("refExterne.equals=" + DEFAULT_REF_EXTERNE, "refExterne.equals=" + UPDATED_REF_EXTERNE);
    }

    @Test
    @Transactional
    void getAllParametersByRefExterneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where refExterne in
        defaultParameterFiltering(
            "refExterne.in=" + DEFAULT_REF_EXTERNE + "," + UPDATED_REF_EXTERNE,
            "refExterne.in=" + UPDATED_REF_EXTERNE
        );
    }

    @Test
    @Transactional
    void getAllParametersByRefExterneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where refExterne is not null
        defaultParameterFiltering("refExterne.specified=true", "refExterne.specified=false");
    }

    @Test
    @Transactional
    void getAllParametersByRefExterneContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where refExterne contains
        defaultParameterFiltering("refExterne.contains=" + DEFAULT_REF_EXTERNE, "refExterne.contains=" + UPDATED_REF_EXTERNE);
    }

    @Test
    @Transactional
    void getAllParametersByRefExterneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where refExterne does not contain
        defaultParameterFiltering("refExterne.doesNotContain=" + UPDATED_REF_EXTERNE, "refExterne.doesNotContain=" + DEFAULT_REF_EXTERNE);
    }

    @Test
    @Transactional
    void getAllParametersByVal1IsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val1 equals to
        defaultParameterFiltering("val1.equals=" + DEFAULT_VAL_1, "val1.equals=" + UPDATED_VAL_1);
    }

    @Test
    @Transactional
    void getAllParametersByVal1IsInShouldWork() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val1 in
        defaultParameterFiltering("val1.in=" + DEFAULT_VAL_1 + "," + UPDATED_VAL_1, "val1.in=" + UPDATED_VAL_1);
    }

    @Test
    @Transactional
    void getAllParametersByVal1IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val1 is not null
        defaultParameterFiltering("val1.specified=true", "val1.specified=false");
    }

    @Test
    @Transactional
    void getAllParametersByVal1ContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val1 contains
        defaultParameterFiltering("val1.contains=" + DEFAULT_VAL_1, "val1.contains=" + UPDATED_VAL_1);
    }

    @Test
    @Transactional
    void getAllParametersByVal1NotContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val1 does not contain
        defaultParameterFiltering("val1.doesNotContain=" + UPDATED_VAL_1, "val1.doesNotContain=" + DEFAULT_VAL_1);
    }

    @Test
    @Transactional
    void getAllParametersByVal2IsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val2 equals to
        defaultParameterFiltering("val2.equals=" + DEFAULT_VAL_2, "val2.equals=" + UPDATED_VAL_2);
    }

    @Test
    @Transactional
    void getAllParametersByVal2IsInShouldWork() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val2 in
        defaultParameterFiltering("val2.in=" + DEFAULT_VAL_2 + "," + UPDATED_VAL_2, "val2.in=" + UPDATED_VAL_2);
    }

    @Test
    @Transactional
    void getAllParametersByVal2IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val2 is not null
        defaultParameterFiltering("val2.specified=true", "val2.specified=false");
    }

    @Test
    @Transactional
    void getAllParametersByVal2ContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val2 contains
        defaultParameterFiltering("val2.contains=" + DEFAULT_VAL_2, "val2.contains=" + UPDATED_VAL_2);
    }

    @Test
    @Transactional
    void getAllParametersByVal2NotContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val2 does not contain
        defaultParameterFiltering("val2.doesNotContain=" + UPDATED_VAL_2, "val2.doesNotContain=" + DEFAULT_VAL_2);
    }

    @Test
    @Transactional
    void getAllParametersByVal3IsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val3 equals to
        defaultParameterFiltering("val3.equals=" + DEFAULT_VAL_3, "val3.equals=" + UPDATED_VAL_3);
    }

    @Test
    @Transactional
    void getAllParametersByVal3IsInShouldWork() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val3 in
        defaultParameterFiltering("val3.in=" + DEFAULT_VAL_3 + "," + UPDATED_VAL_3, "val3.in=" + UPDATED_VAL_3);
    }

    @Test
    @Transactional
    void getAllParametersByVal3IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val3 is not null
        defaultParameterFiltering("val3.specified=true", "val3.specified=false");
    }

    @Test
    @Transactional
    void getAllParametersByVal3ContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val3 contains
        defaultParameterFiltering("val3.contains=" + DEFAULT_VAL_3, "val3.contains=" + UPDATED_VAL_3);
    }

    @Test
    @Transactional
    void getAllParametersByVal3NotContainsSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where val3 does not contain
        defaultParameterFiltering("val3.doesNotContain=" + UPDATED_VAL_3, "val3.doesNotContain=" + DEFAULT_VAL_3);
    }

    @Test
    @Transactional
    void getAllParametersByOrdreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where ordre equals to
        defaultParameterFiltering("ordre.equals=" + DEFAULT_ORDRE, "ordre.equals=" + UPDATED_ORDRE);
    }

    @Test
    @Transactional
    void getAllParametersByOrdreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where ordre in
        defaultParameterFiltering("ordre.in=" + DEFAULT_ORDRE + "," + UPDATED_ORDRE, "ordre.in=" + UPDATED_ORDRE);
    }

    @Test
    @Transactional
    void getAllParametersByOrdreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where ordre is not null
        defaultParameterFiltering("ordre.specified=true", "ordre.specified=false");
    }

    @Test
    @Transactional
    void getAllParametersByOrdreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where ordre is greater than or equal to
        defaultParameterFiltering("ordre.greaterThanOrEqual=" + DEFAULT_ORDRE, "ordre.greaterThanOrEqual=" + UPDATED_ORDRE);
    }

    @Test
    @Transactional
    void getAllParametersByOrdreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where ordre is less than or equal to
        defaultParameterFiltering("ordre.lessThanOrEqual=" + DEFAULT_ORDRE, "ordre.lessThanOrEqual=" + SMALLER_ORDRE);
    }

    @Test
    @Transactional
    void getAllParametersByOrdreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where ordre is less than
        defaultParameterFiltering("ordre.lessThan=" + UPDATED_ORDRE, "ordre.lessThan=" + DEFAULT_ORDRE);
    }

    @Test
    @Transactional
    void getAllParametersByOrdreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList where ordre is greater than
        defaultParameterFiltering("ordre.greaterThan=" + SMALLER_ORDRE, "ordre.greaterThan=" + DEFAULT_ORDRE);
    }

    @Test
    @Transactional
    void getAllParametersByTypeIsEqualToSomething() throws Exception {
        Parameter type;
        if (TestUtil.findAll(em, Parameter.class).isEmpty()) {
            parameterRepository.saveAndFlush(parameter);
            type = ParameterResourceIT.createEntity();
        } else {
            type = TestUtil.findAll(em, Parameter.class).get(0);
        }
        em.persist(type);
        em.flush();
        parameter.setType(type);
        parameterRepository.saveAndFlush(parameter);
        Long typeId = type.getId();
        // Get all the parameterList where type equals to typeId
        defaultParameterShouldBeFound("typeId.equals=" + typeId);

        // Get all the parameterList where type equals to (typeId + 1)
        defaultParameterShouldNotBeFound("typeId.equals=" + (typeId + 1));
    }

    @Test
    @Transactional
    void getAllParametersByParaentIsEqualToSomething() throws Exception {
        Parameter paraent;
        if (TestUtil.findAll(em, Parameter.class).isEmpty()) {
            parameterRepository.saveAndFlush(parameter);
            paraent = ParameterResourceIT.createEntity();
        } else {
            paraent = TestUtil.findAll(em, Parameter.class).get(0);
        }
        em.persist(paraent);
        em.flush();
        parameter.setParaent(paraent);
        parameterRepository.saveAndFlush(parameter);
        Long paraentId = paraent.getId();
        // Get all the parameterList where paraent equals to paraentId
        defaultParameterShouldBeFound("paraentId.equals=" + paraentId);

        // Get all the parameterList where paraent equals to (paraentId + 1)
        defaultParameterShouldNotBeFound("paraentId.equals=" + (paraentId + 1));
    }

    private void defaultParameterFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultParameterShouldBeFound(shouldBeFound);
        defaultParameterShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultParameterShouldBeFound(String filter) throws Exception {
        restParameterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parameter.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].lib2").value(hasItem(DEFAULT_LIB_2)))
            .andExpect(jsonPath("$.[*].lib3").value(hasItem(DEFAULT_LIB_3)))
            .andExpect(jsonPath("$.[*].refExterne").value(hasItem(DEFAULT_REF_EXTERNE)))
            .andExpect(jsonPath("$.[*].val1").value(hasItem(DEFAULT_VAL_1)))
            .andExpect(jsonPath("$.[*].val2").value(hasItem(DEFAULT_VAL_2)))
            .andExpect(jsonPath("$.[*].val3").value(hasItem(DEFAULT_VAL_3)))
            .andExpect(jsonPath("$.[*].ordre").value(hasItem(DEFAULT_ORDRE)));

        // Check, that the count call also returns 1
        restParameterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultParameterShouldNotBeFound(String filter) throws Exception {
        restParameterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restParameterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingParameter() throws Exception {
        // Get the parameter
        restParameterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParameter() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parameter
        Parameter updatedParameter = parameterRepository.findById(parameter.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParameter are not directly saved in db
        em.detach(updatedParameter);
        updatedParameter
            .label(UPDATED_LABEL)
            .activated(UPDATED_ACTIVATED)
            .lib2(UPDATED_LIB_2)
            .lib3(UPDATED_LIB_3)
            .refExterne(UPDATED_REF_EXTERNE)
            .val1(UPDATED_VAL_1)
            .val2(UPDATED_VAL_2)
            .val3(UPDATED_VAL_3)
            .ordre(UPDATED_ORDRE);

        restParameterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedParameter.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedParameter))
            )
            .andExpect(status().isOk());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParameterToMatchAllProperties(updatedParameter);
    }

    @Test
    @Transactional
    void putNonExistingParameter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parameter.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParameterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parameter.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parameter))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParameter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parameter.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParameterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parameter))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParameter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parameter.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParameterMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parameter)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParameterWithPatch() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parameter using partial update
        Parameter partialUpdatedParameter = new Parameter();
        partialUpdatedParameter.setId(parameter.getId());

        partialUpdatedParameter.lib2(UPDATED_LIB_2).lib3(UPDATED_LIB_3).val1(UPDATED_VAL_1);

        restParameterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParameter.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParameter))
            )
            .andExpect(status().isOk());

        // Validate the Parameter in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParameterUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedParameter, parameter),
            getPersistedParameter(parameter)
        );
    }

    @Test
    @Transactional
    void fullUpdateParameterWithPatch() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parameter using partial update
        Parameter partialUpdatedParameter = new Parameter();
        partialUpdatedParameter.setId(parameter.getId());

        partialUpdatedParameter
            .label(UPDATED_LABEL)
            .activated(UPDATED_ACTIVATED)
            .lib2(UPDATED_LIB_2)
            .lib3(UPDATED_LIB_3)
            .refExterne(UPDATED_REF_EXTERNE)
            .val1(UPDATED_VAL_1)
            .val2(UPDATED_VAL_2)
            .val3(UPDATED_VAL_3)
            .ordre(UPDATED_ORDRE);

        restParameterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParameter.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParameter))
            )
            .andExpect(status().isOk());

        // Validate the Parameter in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParameterUpdatableFieldsEquals(partialUpdatedParameter, getPersistedParameter(partialUpdatedParameter));
    }

    @Test
    @Transactional
    void patchNonExistingParameter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parameter.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParameterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, parameter.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(parameter))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParameter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parameter.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParameterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(parameter))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParameter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parameter.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParameterMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(parameter))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParameter() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the parameter
        restParameterMockMvc
            .perform(delete(ENTITY_API_URL_ID, parameter.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return parameterRepository.count();
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

    protected Parameter getPersistedParameter(Parameter parameter) {
        return parameterRepository.findById(parameter.getId()).orElseThrow();
    }

    protected void assertPersistedParameterToMatchAllProperties(Parameter expectedParameter) {
        assertParameterAllPropertiesEquals(expectedParameter, getPersistedParameter(expectedParameter));
    }

    protected void assertPersistedParameterToMatchUpdatableProperties(Parameter expectedParameter) {
        assertParameterAllUpdatablePropertiesEquals(expectedParameter, getPersistedParameter(expectedParameter));
    }
}
