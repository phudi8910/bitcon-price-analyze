package myapp.bitcoin_analyze.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;
import jakarta.persistence.EntityManager;
import myapp.bitcoin_analyze.IntegrationTest;
import myapp.bitcoin_analyze.domain.BitcoinExchange;
import myapp.bitcoin_analyze.domain.MarketApi;
import myapp.bitcoin_analyze.repository.MarketApiRepository;
import myapp.bitcoin_analyze.service.dto.MarketApiDTO;
import myapp.bitcoin_analyze.service.mapper.MarketApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MarketApiResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MarketApiResourceIT {

    private static final String DEFAULT_MARKET_NAME = "AAAAAAAAAA";
    private static final String UPDATED_MARKET_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_IS_ACTIVE = "AAAAAAAAAA";
    private static final String UPDATED_IS_ACTIVE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORD = 1;
    private static final Integer UPDATED_ORD = 2;
    private static final Integer SMALLER_ORD = 1 - 1;

    private static final String ENTITY_API_URL = "/api/market-apis";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private MarketApiRepository marketApiRepository;

    @Autowired
    private MarketApiMapper marketApiMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMarketApiMockMvc;

    private MarketApi marketApi;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MarketApi createEntity(EntityManager em) {
        MarketApi marketApi = new MarketApi()
            .marketName(DEFAULT_MARKET_NAME)
            .url(DEFAULT_URL)
            .isActive(DEFAULT_IS_ACTIVE)
            .description(DEFAULT_DESCRIPTION)
            .ord(DEFAULT_ORD);
        return marketApi;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MarketApi createUpdatedEntity(EntityManager em) {
        MarketApi marketApi = new MarketApi()
            .marketName(UPDATED_MARKET_NAME)
            .url(UPDATED_URL)
            .isActive(UPDATED_IS_ACTIVE)
            .description(UPDATED_DESCRIPTION)
            .ord(UPDATED_ORD);
        return marketApi;
    }

    @BeforeEach
    public void initTest() {
        marketApi = createEntity(em);
    }

    @Test
    @Transactional
    void createMarketApi() throws Exception {
        int databaseSizeBeforeCreate = marketApiRepository.findAll().size();
        // Create the MarketApi
        MarketApiDTO marketApiDTO = marketApiMapper.toDto(marketApi);
        restMarketApiMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(marketApiDTO))
            )
            .andExpect(status().isCreated());

        // Validate the MarketApi in the database
        List<MarketApi> marketApiList = marketApiRepository.findAll();
        assertThat(marketApiList).hasSize(databaseSizeBeforeCreate + 1);
        MarketApi testMarketApi = marketApiList.get(marketApiList.size() - 1);
        assertThat(testMarketApi.getMarketName()).isEqualTo(DEFAULT_MARKET_NAME);
        assertThat(testMarketApi.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testMarketApi.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testMarketApi.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMarketApi.getOrd()).isEqualTo(DEFAULT_ORD);
    }

    @Test
    @Transactional
    void createMarketApiWithExistingId() throws Exception {
        // Create the MarketApi with an existing ID
        marketApi.setId("existing_id");
        MarketApiDTO marketApiDTO = marketApiMapper.toDto(marketApi);

        int databaseSizeBeforeCreate = marketApiRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMarketApiMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(marketApiDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MarketApi in the database
        List<MarketApi> marketApiList = marketApiRepository.findAll();
        assertThat(marketApiList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMarketApis() throws Exception {
        // Initialize the database
        marketApi.setId(UUID.randomUUID().toString());
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList
        restMarketApiMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(marketApi.getId())))
            .andExpect(jsonPath("$.[*].marketName").value(hasItem(DEFAULT_MARKET_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].ord").value(hasItem(DEFAULT_ORD)));
    }

    @Test
    @Transactional
    void getMarketApi() throws Exception {
        // Initialize the database
        marketApi.setId(UUID.randomUUID().toString());
        marketApiRepository.saveAndFlush(marketApi);

        // Get the marketApi
        restMarketApiMockMvc
            .perform(get(ENTITY_API_URL_ID, marketApi.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(marketApi.getId()))
            .andExpect(jsonPath("$.marketName").value(DEFAULT_MARKET_NAME))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.ord").value(DEFAULT_ORD));
    }

    @Test
    @Transactional
    void getMarketApisByIdFiltering() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        String id = marketApi.getId();

        defaultMarketApiShouldBeFound("id.equals=" + id);
        defaultMarketApiShouldNotBeFound("id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllMarketApisByMarketNameIsEqualToSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where marketName equals to DEFAULT_MARKET_NAME
        defaultMarketApiShouldBeFound("marketName.equals=" + DEFAULT_MARKET_NAME);

        // Get all the marketApiList where marketName equals to UPDATED_MARKET_NAME
        defaultMarketApiShouldNotBeFound("marketName.equals=" + UPDATED_MARKET_NAME);
    }

    @Test
    @Transactional
    void getAllMarketApisByMarketNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where marketName not equals to DEFAULT_MARKET_NAME
        defaultMarketApiShouldNotBeFound("marketName.notEquals=" + DEFAULT_MARKET_NAME);

        // Get all the marketApiList where marketName not equals to UPDATED_MARKET_NAME
        defaultMarketApiShouldBeFound("marketName.notEquals=" + UPDATED_MARKET_NAME);
    }

    @Test
    @Transactional
    void getAllMarketApisByMarketNameIsInShouldWork() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where marketName in DEFAULT_MARKET_NAME or UPDATED_MARKET_NAME
        defaultMarketApiShouldBeFound("marketName.in=" + DEFAULT_MARKET_NAME + "," + UPDATED_MARKET_NAME);

        // Get all the marketApiList where marketName equals to UPDATED_MARKET_NAME
        defaultMarketApiShouldNotBeFound("marketName.in=" + UPDATED_MARKET_NAME);
    }

    @Test
    @Transactional
    void getAllMarketApisByMarketNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where marketName is not null
        defaultMarketApiShouldBeFound("marketName.specified=true");

        // Get all the marketApiList where marketName is null
        defaultMarketApiShouldNotBeFound("marketName.specified=false");
    }

    @Test
    @Transactional
    void getAllMarketApisByMarketNameContainsSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where marketName contains DEFAULT_MARKET_NAME
        defaultMarketApiShouldBeFound("marketName.contains=" + DEFAULT_MARKET_NAME);

        // Get all the marketApiList where marketName contains UPDATED_MARKET_NAME
        defaultMarketApiShouldNotBeFound("marketName.contains=" + UPDATED_MARKET_NAME);
    }

    @Test
    @Transactional
    void getAllMarketApisByMarketNameNotContainsSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where marketName does not contain DEFAULT_MARKET_NAME
        defaultMarketApiShouldNotBeFound("marketName.doesNotContain=" + DEFAULT_MARKET_NAME);

        // Get all the marketApiList where marketName does not contain UPDATED_MARKET_NAME
        defaultMarketApiShouldBeFound("marketName.doesNotContain=" + UPDATED_MARKET_NAME);
    }

    @Test
    @Transactional
    void getAllMarketApisByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where url equals to DEFAULT_URL
        defaultMarketApiShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the marketApiList where url equals to UPDATED_URL
        defaultMarketApiShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllMarketApisByUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where url not equals to DEFAULT_URL
        defaultMarketApiShouldNotBeFound("url.notEquals=" + DEFAULT_URL);

        // Get all the marketApiList where url not equals to UPDATED_URL
        defaultMarketApiShouldBeFound("url.notEquals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllMarketApisByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where url in DEFAULT_URL or UPDATED_URL
        defaultMarketApiShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the marketApiList where url equals to UPDATED_URL
        defaultMarketApiShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllMarketApisByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where url is not null
        defaultMarketApiShouldBeFound("url.specified=true");

        // Get all the marketApiList where url is null
        defaultMarketApiShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    void getAllMarketApisByUrlContainsSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where url contains DEFAULT_URL
        defaultMarketApiShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the marketApiList where url contains UPDATED_URL
        defaultMarketApiShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllMarketApisByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where url does not contain DEFAULT_URL
        defaultMarketApiShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the marketApiList where url does not contain UPDATED_URL
        defaultMarketApiShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllMarketApisByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where isActive equals to DEFAULT_IS_ACTIVE
        defaultMarketApiShouldBeFound("isActive.equals=" + DEFAULT_IS_ACTIVE);

        // Get all the marketApiList where isActive equals to UPDATED_IS_ACTIVE
        defaultMarketApiShouldNotBeFound("isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMarketApisByIsActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where isActive not equals to DEFAULT_IS_ACTIVE
        defaultMarketApiShouldNotBeFound("isActive.notEquals=" + DEFAULT_IS_ACTIVE);

        // Get all the marketApiList where isActive not equals to UPDATED_IS_ACTIVE
        defaultMarketApiShouldBeFound("isActive.notEquals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMarketApisByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where isActive in DEFAULT_IS_ACTIVE or UPDATED_IS_ACTIVE
        defaultMarketApiShouldBeFound("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE);

        // Get all the marketApiList where isActive equals to UPDATED_IS_ACTIVE
        defaultMarketApiShouldNotBeFound("isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMarketApisByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where isActive is not null
        defaultMarketApiShouldBeFound("isActive.specified=true");

        // Get all the marketApiList where isActive is null
        defaultMarketApiShouldNotBeFound("isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllMarketApisByIsActiveContainsSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where isActive contains DEFAULT_IS_ACTIVE
        defaultMarketApiShouldBeFound("isActive.contains=" + DEFAULT_IS_ACTIVE);

        // Get all the marketApiList where isActive contains UPDATED_IS_ACTIVE
        defaultMarketApiShouldNotBeFound("isActive.contains=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMarketApisByIsActiveNotContainsSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where isActive does not contain DEFAULT_IS_ACTIVE
        defaultMarketApiShouldNotBeFound("isActive.doesNotContain=" + DEFAULT_IS_ACTIVE);

        // Get all the marketApiList where isActive does not contain UPDATED_IS_ACTIVE
        defaultMarketApiShouldBeFound("isActive.doesNotContain=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMarketApisByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where description equals to DEFAULT_DESCRIPTION
        defaultMarketApiShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the marketApiList where description equals to UPDATED_DESCRIPTION
        defaultMarketApiShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMarketApisByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where description not equals to DEFAULT_DESCRIPTION
        defaultMarketApiShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the marketApiList where description not equals to UPDATED_DESCRIPTION
        defaultMarketApiShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMarketApisByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultMarketApiShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the marketApiList where description equals to UPDATED_DESCRIPTION
        defaultMarketApiShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMarketApisByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where description is not null
        defaultMarketApiShouldBeFound("description.specified=true");

        // Get all the marketApiList where description is null
        defaultMarketApiShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllMarketApisByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where description contains DEFAULT_DESCRIPTION
        defaultMarketApiShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the marketApiList where description contains UPDATED_DESCRIPTION
        defaultMarketApiShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMarketApisByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where description does not contain DEFAULT_DESCRIPTION
        defaultMarketApiShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the marketApiList where description does not contain UPDATED_DESCRIPTION
        defaultMarketApiShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMarketApisByOrdIsEqualToSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where ord equals to DEFAULT_ORD
        defaultMarketApiShouldBeFound("ord.equals=" + DEFAULT_ORD);

        // Get all the marketApiList where ord equals to UPDATED_ORD
        defaultMarketApiShouldNotBeFound("ord.equals=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllMarketApisByOrdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where ord not equals to DEFAULT_ORD
        defaultMarketApiShouldNotBeFound("ord.notEquals=" + DEFAULT_ORD);

        // Get all the marketApiList where ord not equals to UPDATED_ORD
        defaultMarketApiShouldBeFound("ord.notEquals=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllMarketApisByOrdIsInShouldWork() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where ord in DEFAULT_ORD or UPDATED_ORD
        defaultMarketApiShouldBeFound("ord.in=" + DEFAULT_ORD + "," + UPDATED_ORD);

        // Get all the marketApiList where ord equals to UPDATED_ORD
        defaultMarketApiShouldNotBeFound("ord.in=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllMarketApisByOrdIsNullOrNotNull() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where ord is not null
        defaultMarketApiShouldBeFound("ord.specified=true");

        // Get all the marketApiList where ord is null
        defaultMarketApiShouldNotBeFound("ord.specified=false");
    }

    @Test
    @Transactional
    void getAllMarketApisByOrdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where ord is greater than or equal to DEFAULT_ORD
        defaultMarketApiShouldBeFound("ord.greaterThanOrEqual=" + DEFAULT_ORD);

        // Get all the marketApiList where ord is greater than or equal to UPDATED_ORD
        defaultMarketApiShouldNotBeFound("ord.greaterThanOrEqual=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllMarketApisByOrdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where ord is less than or equal to DEFAULT_ORD
        defaultMarketApiShouldBeFound("ord.lessThanOrEqual=" + DEFAULT_ORD);

        // Get all the marketApiList where ord is less than or equal to SMALLER_ORD
        defaultMarketApiShouldNotBeFound("ord.lessThanOrEqual=" + SMALLER_ORD);
    }

    @Test
    @Transactional
    void getAllMarketApisByOrdIsLessThanSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where ord is less than DEFAULT_ORD
        defaultMarketApiShouldNotBeFound("ord.lessThan=" + DEFAULT_ORD);

        // Get all the marketApiList where ord is less than UPDATED_ORD
        defaultMarketApiShouldBeFound("ord.lessThan=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllMarketApisByOrdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        marketApiRepository.saveAndFlush(marketApi);

        // Get all the marketApiList where ord is greater than DEFAULT_ORD
        defaultMarketApiShouldNotBeFound("ord.greaterThan=" + DEFAULT_ORD);

        // Get all the marketApiList where ord is greater than SMALLER_ORD
        defaultMarketApiShouldBeFound("ord.greaterThan=" + SMALLER_ORD);
    }
    
    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMarketApiShouldBeFound(String filter) throws Exception {
        restMarketApiMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(marketApi.getId())))
            .andExpect(jsonPath("$.[*].marketName").value(hasItem(DEFAULT_MARKET_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].ord").value(hasItem(DEFAULT_ORD)));

        // Check, that the count call also returns 1
        restMarketApiMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMarketApiShouldNotBeFound(String filter) throws Exception {
        restMarketApiMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMarketApiMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMarketApi() throws Exception {
        // Get the marketApi
        restMarketApiMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMarketApi() throws Exception {
        // Initialize the database
        marketApi.setId(UUID.randomUUID().toString());
        marketApiRepository.saveAndFlush(marketApi);

        int databaseSizeBeforeUpdate = marketApiRepository.findAll().size();

        // Update the marketApi
        MarketApi updatedMarketApi = marketApiRepository.findById(marketApi.getId()).get();
        // Disconnect from session so that the updates on updatedMarketApi are not directly saved in db
        em.detach(updatedMarketApi);
        updatedMarketApi
            .marketName(UPDATED_MARKET_NAME)
            .url(UPDATED_URL)
            .isActive(UPDATED_IS_ACTIVE)
            .description(UPDATED_DESCRIPTION)
            .ord(UPDATED_ORD);
        MarketApiDTO marketApiDTO = marketApiMapper.toDto(updatedMarketApi);

        restMarketApiMockMvc
            .perform(
                put(ENTITY_API_URL_ID, marketApiDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(marketApiDTO))
            )
            .andExpect(status().isOk());

        // Validate the MarketApi in the database
        List<MarketApi> marketApiList = marketApiRepository.findAll();
        assertThat(marketApiList).hasSize(databaseSizeBeforeUpdate);
        MarketApi testMarketApi = marketApiList.get(marketApiList.size() - 1);
        assertThat(testMarketApi.getMarketName()).isEqualTo(UPDATED_MARKET_NAME);
        assertThat(testMarketApi.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testMarketApi.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testMarketApi.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMarketApi.getOrd()).isEqualTo(UPDATED_ORD);
    }

    @Test
    @Transactional
    void putNonExistingMarketApi() throws Exception {
        int databaseSizeBeforeUpdate = marketApiRepository.findAll().size();
        marketApi.setId(UUID.randomUUID().toString());

        // Create the MarketApi
        MarketApiDTO marketApiDTO = marketApiMapper.toDto(marketApi);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMarketApiMockMvc
            .perform(
                put(ENTITY_API_URL_ID, marketApiDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(marketApiDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MarketApi in the database
        List<MarketApi> marketApiList = marketApiRepository.findAll();
        assertThat(marketApiList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMarketApi() throws Exception {
        int databaseSizeBeforeUpdate = marketApiRepository.findAll().size();
        marketApi.setId(UUID.randomUUID().toString());

        // Create the MarketApi
        MarketApiDTO marketApiDTO = marketApiMapper.toDto(marketApi);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMarketApiMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(marketApiDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MarketApi in the database
        List<MarketApi> marketApiList = marketApiRepository.findAll();
        assertThat(marketApiList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMarketApi() throws Exception {
        int databaseSizeBeforeUpdate = marketApiRepository.findAll().size();
        marketApi.setId(UUID.randomUUID().toString());

        // Create the MarketApi
        MarketApiDTO marketApiDTO = marketApiMapper.toDto(marketApi);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMarketApiMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(marketApiDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MarketApi in the database
        List<MarketApi> marketApiList = marketApiRepository.findAll();
        assertThat(marketApiList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMarketApiWithPatch() throws Exception {
        // Initialize the database
        marketApi.setId(UUID.randomUUID().toString());
        marketApiRepository.saveAndFlush(marketApi);

        int databaseSizeBeforeUpdate = marketApiRepository.findAll().size();

        // Update the marketApi using partial update
        MarketApi partialUpdatedMarketApi = new MarketApi();
        partialUpdatedMarketApi.setId(marketApi.getId());

        partialUpdatedMarketApi.marketName(UPDATED_MARKET_NAME).isActive(UPDATED_IS_ACTIVE);

        restMarketApiMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMarketApi.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMarketApi))
            )
            .andExpect(status().isOk());

        // Validate the MarketApi in the database
        List<MarketApi> marketApiList = marketApiRepository.findAll();
        assertThat(marketApiList).hasSize(databaseSizeBeforeUpdate);
        MarketApi testMarketApi = marketApiList.get(marketApiList.size() - 1);
        assertThat(testMarketApi.getMarketName()).isEqualTo(UPDATED_MARKET_NAME);
        assertThat(testMarketApi.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testMarketApi.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testMarketApi.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMarketApi.getOrd()).isEqualTo(DEFAULT_ORD);
    }

    @Test
    @Transactional
    void fullUpdateMarketApiWithPatch() throws Exception {
        // Initialize the database
        marketApi.setId(UUID.randomUUID().toString());
        marketApiRepository.saveAndFlush(marketApi);

        int databaseSizeBeforeUpdate = marketApiRepository.findAll().size();

        // Update the marketApi using partial update
        MarketApi partialUpdatedMarketApi = new MarketApi();
        partialUpdatedMarketApi.setId(marketApi.getId());

        partialUpdatedMarketApi
            .marketName(UPDATED_MARKET_NAME)
            .url(UPDATED_URL)
            .isActive(UPDATED_IS_ACTIVE)
            .description(UPDATED_DESCRIPTION)
            .ord(UPDATED_ORD);

        restMarketApiMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMarketApi.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMarketApi))
            )
            .andExpect(status().isOk());

        // Validate the MarketApi in the database
        List<MarketApi> marketApiList = marketApiRepository.findAll();
        assertThat(marketApiList).hasSize(databaseSizeBeforeUpdate);
        MarketApi testMarketApi = marketApiList.get(marketApiList.size() - 1);
        assertThat(testMarketApi.getMarketName()).isEqualTo(UPDATED_MARKET_NAME);
        assertThat(testMarketApi.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testMarketApi.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testMarketApi.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMarketApi.getOrd()).isEqualTo(UPDATED_ORD);
    }

    @Test
    @Transactional
    void patchNonExistingMarketApi() throws Exception {
        int databaseSizeBeforeUpdate = marketApiRepository.findAll().size();
        marketApi.setId(UUID.randomUUID().toString());

        // Create the MarketApi
        MarketApiDTO marketApiDTO = marketApiMapper.toDto(marketApi);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMarketApiMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, marketApiDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(marketApiDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MarketApi in the database
        List<MarketApi> marketApiList = marketApiRepository.findAll();
        assertThat(marketApiList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMarketApi() throws Exception {
        int databaseSizeBeforeUpdate = marketApiRepository.findAll().size();
        marketApi.setId(UUID.randomUUID().toString());

        // Create the MarketApi
        MarketApiDTO marketApiDTO = marketApiMapper.toDto(marketApi);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMarketApiMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(marketApiDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MarketApi in the database
        List<MarketApi> marketApiList = marketApiRepository.findAll();
        assertThat(marketApiList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMarketApi() throws Exception {
        int databaseSizeBeforeUpdate = marketApiRepository.findAll().size();
        marketApi.setId(UUID.randomUUID().toString());

        // Create the MarketApi
        MarketApiDTO marketApiDTO = marketApiMapper.toDto(marketApi);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMarketApiMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(marketApiDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MarketApi in the database
        List<MarketApi> marketApiList = marketApiRepository.findAll();
        assertThat(marketApiList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMarketApi() throws Exception {
        // Initialize the database
        marketApi.setId(UUID.randomUUID().toString());
        marketApiRepository.saveAndFlush(marketApi);

        int databaseSizeBeforeDelete = marketApiRepository.findAll().size();

        // Delete the marketApi
        restMarketApiMockMvc
            .perform(delete(ENTITY_API_URL_ID, marketApi.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<MarketApi> marketApiList = marketApiRepository.findAll();
        assertThat(marketApiList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
