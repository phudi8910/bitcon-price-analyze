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
import myapp.bitcoin_analyze.domain.CurrencyList;
import myapp.bitcoin_analyze.repository.CurrencyListRepository;
import myapp.bitcoin_analyze.service.dto.CurrencyListDTO;
import myapp.bitcoin_analyze.service.mapper.CurrencyListMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CurrencyListResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CurrencyListResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_IS_ACTIVE = "AAAAAAAAAA";
    private static final String UPDATED_IS_ACTIVE = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORD = 1;
    private static final Integer UPDATED_ORD = 2;
    private static final Integer SMALLER_ORD = 1 - 1;

    private static final String ENTITY_API_URL = "/api/currency-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private CurrencyListRepository currencyListRepository;

    @Autowired
    private CurrencyListMapper currencyListMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCurrencyListMockMvc;

    private CurrencyList currencyList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CurrencyList createEntity(EntityManager em) {
        CurrencyList currencyList = new CurrencyList().description(DEFAULT_DESCRIPTION).isActive(DEFAULT_IS_ACTIVE).ord(DEFAULT_ORD);
        return currencyList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CurrencyList createUpdatedEntity(EntityManager em) {
        CurrencyList currencyList = new CurrencyList().description(UPDATED_DESCRIPTION).isActive(UPDATED_IS_ACTIVE).ord(UPDATED_ORD);
        return currencyList;
    }

    @BeforeEach
    public void initTest() {
        currencyList = createEntity(em);
    }

    @Test
    @Transactional
    void createCurrencyList() throws Exception {
        int databaseSizeBeforeCreate = currencyListRepository.findAll().size();
        // Create the CurrencyList
        CurrencyListDTO currencyListDTO = currencyListMapper.toDto(currencyList);
        restCurrencyListMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(currencyListDTO))
            )
            .andExpect(status().isCreated());

        // Validate the CurrencyList in the database
        List<CurrencyList> currencyListList = currencyListRepository.findAll();
        assertThat(currencyListList).hasSize(databaseSizeBeforeCreate + 1);
        CurrencyList testCurrencyList = currencyListList.get(currencyListList.size() - 1);
        assertThat(testCurrencyList.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCurrencyList.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testCurrencyList.getOrd()).isEqualTo(DEFAULT_ORD);
    }

    @Test
    @Transactional
    void createCurrencyListWithExistingId() throws Exception {
        // Create the CurrencyList with an existing ID
        currencyList.setId("existing_id");
        CurrencyListDTO currencyListDTO = currencyListMapper.toDto(currencyList);

        int databaseSizeBeforeCreate = currencyListRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCurrencyListMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(currencyListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CurrencyList in the database
        List<CurrencyList> currencyListList = currencyListRepository.findAll();
        assertThat(currencyListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCurrencyLists() throws Exception {
        // Initialize the database
        currencyList.setId(UUID.randomUUID().toString());
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList
        restCurrencyListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(currencyList.getId())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].ord").value(hasItem(DEFAULT_ORD)));
    }

    @Test
    @Transactional
    void getCurrencyList() throws Exception {
        // Initialize the database
        currencyList.setId(UUID.randomUUID().toString());
        currencyListRepository.saveAndFlush(currencyList);

        // Get the currencyList
        restCurrencyListMockMvc
            .perform(get(ENTITY_API_URL_ID, currencyList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(currencyList.getId()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.ord").value(DEFAULT_ORD));
    }

    @Test
    @Transactional
    void getCurrencyListsByIdFiltering() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        String id = currencyList.getId();

        defaultCurrencyListShouldBeFound("id.equals=" + id);
        defaultCurrencyListShouldNotBeFound("id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where description equals to DEFAULT_DESCRIPTION
        defaultCurrencyListShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the currencyListList where description equals to UPDATED_DESCRIPTION
        defaultCurrencyListShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where description not equals to DEFAULT_DESCRIPTION
        defaultCurrencyListShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the currencyListList where description not equals to UPDATED_DESCRIPTION
        defaultCurrencyListShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultCurrencyListShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the currencyListList where description equals to UPDATED_DESCRIPTION
        defaultCurrencyListShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where description is not null
        defaultCurrencyListShouldBeFound("description.specified=true");

        // Get all the currencyListList where description is null
        defaultCurrencyListShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllCurrencyListsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where description contains DEFAULT_DESCRIPTION
        defaultCurrencyListShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the currencyListList where description contains UPDATED_DESCRIPTION
        defaultCurrencyListShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where description does not contain DEFAULT_DESCRIPTION
        defaultCurrencyListShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the currencyListList where description does not contain UPDATED_DESCRIPTION
        defaultCurrencyListShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where isActive equals to DEFAULT_IS_ACTIVE
        defaultCurrencyListShouldBeFound("isActive.equals=" + DEFAULT_IS_ACTIVE);

        // Get all the currencyListList where isActive equals to UPDATED_IS_ACTIVE
        defaultCurrencyListShouldNotBeFound("isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByIsActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where isActive not equals to DEFAULT_IS_ACTIVE
        defaultCurrencyListShouldNotBeFound("isActive.notEquals=" + DEFAULT_IS_ACTIVE);

        // Get all the currencyListList where isActive not equals to UPDATED_IS_ACTIVE
        defaultCurrencyListShouldBeFound("isActive.notEquals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where isActive in DEFAULT_IS_ACTIVE or UPDATED_IS_ACTIVE
        defaultCurrencyListShouldBeFound("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE);

        // Get all the currencyListList where isActive equals to UPDATED_IS_ACTIVE
        defaultCurrencyListShouldNotBeFound("isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where isActive is not null
        defaultCurrencyListShouldBeFound("isActive.specified=true");

        // Get all the currencyListList where isActive is null
        defaultCurrencyListShouldNotBeFound("isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllCurrencyListsByIsActiveContainsSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where isActive contains DEFAULT_IS_ACTIVE
        defaultCurrencyListShouldBeFound("isActive.contains=" + DEFAULT_IS_ACTIVE);

        // Get all the currencyListList where isActive contains UPDATED_IS_ACTIVE
        defaultCurrencyListShouldNotBeFound("isActive.contains=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByIsActiveNotContainsSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where isActive does not contain DEFAULT_IS_ACTIVE
        defaultCurrencyListShouldNotBeFound("isActive.doesNotContain=" + DEFAULT_IS_ACTIVE);

        // Get all the currencyListList where isActive does not contain UPDATED_IS_ACTIVE
        defaultCurrencyListShouldBeFound("isActive.doesNotContain=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByOrdIsEqualToSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where ord equals to DEFAULT_ORD
        defaultCurrencyListShouldBeFound("ord.equals=" + DEFAULT_ORD);

        // Get all the currencyListList where ord equals to UPDATED_ORD
        defaultCurrencyListShouldNotBeFound("ord.equals=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByOrdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where ord not equals to DEFAULT_ORD
        defaultCurrencyListShouldNotBeFound("ord.notEquals=" + DEFAULT_ORD);

        // Get all the currencyListList where ord not equals to UPDATED_ORD
        defaultCurrencyListShouldBeFound("ord.notEquals=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByOrdIsInShouldWork() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where ord in DEFAULT_ORD or UPDATED_ORD
        defaultCurrencyListShouldBeFound("ord.in=" + DEFAULT_ORD + "," + UPDATED_ORD);

        // Get all the currencyListList where ord equals to UPDATED_ORD
        defaultCurrencyListShouldNotBeFound("ord.in=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByOrdIsNullOrNotNull() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where ord is not null
        defaultCurrencyListShouldBeFound("ord.specified=true");

        // Get all the currencyListList where ord is null
        defaultCurrencyListShouldNotBeFound("ord.specified=false");
    }

    @Test
    @Transactional
    void getAllCurrencyListsByOrdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where ord is greater than or equal to DEFAULT_ORD
        defaultCurrencyListShouldBeFound("ord.greaterThanOrEqual=" + DEFAULT_ORD);

        // Get all the currencyListList where ord is greater than or equal to UPDATED_ORD
        defaultCurrencyListShouldNotBeFound("ord.greaterThanOrEqual=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByOrdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where ord is less than or equal to DEFAULT_ORD
        defaultCurrencyListShouldBeFound("ord.lessThanOrEqual=" + DEFAULT_ORD);

        // Get all the currencyListList where ord is less than or equal to SMALLER_ORD
        defaultCurrencyListShouldNotBeFound("ord.lessThanOrEqual=" + SMALLER_ORD);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByOrdIsLessThanSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where ord is less than DEFAULT_ORD
        defaultCurrencyListShouldNotBeFound("ord.lessThan=" + DEFAULT_ORD);

        // Get all the currencyListList where ord is less than UPDATED_ORD
        defaultCurrencyListShouldBeFound("ord.lessThan=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllCurrencyListsByOrdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        currencyListRepository.saveAndFlush(currencyList);

        // Get all the currencyListList where ord is greater than DEFAULT_ORD
        defaultCurrencyListShouldNotBeFound("ord.greaterThan=" + DEFAULT_ORD);

        // Get all the currencyListList where ord is greater than SMALLER_ORD
        defaultCurrencyListShouldBeFound("ord.greaterThan=" + SMALLER_ORD);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCurrencyListShouldBeFound(String filter) throws Exception {
        restCurrencyListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(currencyList.getId())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].ord").value(hasItem(DEFAULT_ORD)));

        // Check, that the count call also returns 1
        restCurrencyListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCurrencyListShouldNotBeFound(String filter) throws Exception {
        restCurrencyListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCurrencyListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCurrencyList() throws Exception {
        // Get the currencyList
        restCurrencyListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCurrencyList() throws Exception {
        // Initialize the database
        currencyList.setId(UUID.randomUUID().toString());
        currencyListRepository.saveAndFlush(currencyList);

        int databaseSizeBeforeUpdate = currencyListRepository.findAll().size();

        // Update the currencyList
        CurrencyList updatedCurrencyList = currencyListRepository.findById(currencyList.getId()).get();
        // Disconnect from session so that the updates on updatedCurrencyList are not directly saved in db
        em.detach(updatedCurrencyList);
        updatedCurrencyList.description(UPDATED_DESCRIPTION).isActive(UPDATED_IS_ACTIVE).ord(UPDATED_ORD);
        CurrencyListDTO currencyListDTO = currencyListMapper.toDto(updatedCurrencyList);

        restCurrencyListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, currencyListDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(currencyListDTO))
            )
            .andExpect(status().isOk());

        // Validate the CurrencyList in the database
        List<CurrencyList> currencyListList = currencyListRepository.findAll();
        assertThat(currencyListList).hasSize(databaseSizeBeforeUpdate);
        CurrencyList testCurrencyList = currencyListList.get(currencyListList.size() - 1);
        assertThat(testCurrencyList.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCurrencyList.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testCurrencyList.getOrd()).isEqualTo(UPDATED_ORD);
    }

    @Test
    @Transactional
    void putNonExistingCurrencyList() throws Exception {
        int databaseSizeBeforeUpdate = currencyListRepository.findAll().size();
        currencyList.setId(UUID.randomUUID().toString());

        // Create the CurrencyList
        CurrencyListDTO currencyListDTO = currencyListMapper.toDto(currencyList);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCurrencyListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, currencyListDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(currencyListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CurrencyList in the database
        List<CurrencyList> currencyListList = currencyListRepository.findAll();
        assertThat(currencyListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCurrencyList() throws Exception {
        int databaseSizeBeforeUpdate = currencyListRepository.findAll().size();
        currencyList.setId(UUID.randomUUID().toString());

        // Create the CurrencyList
        CurrencyListDTO currencyListDTO = currencyListMapper.toDto(currencyList);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCurrencyListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(currencyListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CurrencyList in the database
        List<CurrencyList> currencyListList = currencyListRepository.findAll();
        assertThat(currencyListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCurrencyList() throws Exception {
        int databaseSizeBeforeUpdate = currencyListRepository.findAll().size();
        currencyList.setId(UUID.randomUUID().toString());

        // Create the CurrencyList
        CurrencyListDTO currencyListDTO = currencyListMapper.toDto(currencyList);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCurrencyListMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(currencyListDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CurrencyList in the database
        List<CurrencyList> currencyListList = currencyListRepository.findAll();
        assertThat(currencyListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCurrencyListWithPatch() throws Exception {
        // Initialize the database
        currencyList.setId(UUID.randomUUID().toString());
        currencyListRepository.saveAndFlush(currencyList);

        int databaseSizeBeforeUpdate = currencyListRepository.findAll().size();

        // Update the currencyList using partial update
        CurrencyList partialUpdatedCurrencyList = new CurrencyList();
        partialUpdatedCurrencyList.setId(currencyList.getId());

        partialUpdatedCurrencyList.description(UPDATED_DESCRIPTION).ord(UPDATED_ORD);

        restCurrencyListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCurrencyList.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCurrencyList))
            )
            .andExpect(status().isOk());

        // Validate the CurrencyList in the database
        List<CurrencyList> currencyListList = currencyListRepository.findAll();
        assertThat(currencyListList).hasSize(databaseSizeBeforeUpdate);
        CurrencyList testCurrencyList = currencyListList.get(currencyListList.size() - 1);
        assertThat(testCurrencyList.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCurrencyList.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testCurrencyList.getOrd()).isEqualTo(UPDATED_ORD);
    }

    @Test
    @Transactional
    void fullUpdateCurrencyListWithPatch() throws Exception {
        // Initialize the database
        currencyList.setId(UUID.randomUUID().toString());
        currencyListRepository.saveAndFlush(currencyList);

        int databaseSizeBeforeUpdate = currencyListRepository.findAll().size();

        // Update the currencyList using partial update
        CurrencyList partialUpdatedCurrencyList = new CurrencyList();
        partialUpdatedCurrencyList.setId(currencyList.getId());

        partialUpdatedCurrencyList.description(UPDATED_DESCRIPTION).isActive(UPDATED_IS_ACTIVE).ord(UPDATED_ORD);

        restCurrencyListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCurrencyList.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCurrencyList))
            )
            .andExpect(status().isOk());

        // Validate the CurrencyList in the database
        List<CurrencyList> currencyListList = currencyListRepository.findAll();
        assertThat(currencyListList).hasSize(databaseSizeBeforeUpdate);
        CurrencyList testCurrencyList = currencyListList.get(currencyListList.size() - 1);
        assertThat(testCurrencyList.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCurrencyList.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testCurrencyList.getOrd()).isEqualTo(UPDATED_ORD);
    }

    @Test
    @Transactional
    void patchNonExistingCurrencyList() throws Exception {
        int databaseSizeBeforeUpdate = currencyListRepository.findAll().size();
        currencyList.setId(UUID.randomUUID().toString());

        // Create the CurrencyList
        CurrencyListDTO currencyListDTO = currencyListMapper.toDto(currencyList);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCurrencyListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, currencyListDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(currencyListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CurrencyList in the database
        List<CurrencyList> currencyListList = currencyListRepository.findAll();
        assertThat(currencyListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCurrencyList() throws Exception {
        int databaseSizeBeforeUpdate = currencyListRepository.findAll().size();
        currencyList.setId(UUID.randomUUID().toString());

        // Create the CurrencyList
        CurrencyListDTO currencyListDTO = currencyListMapper.toDto(currencyList);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCurrencyListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(currencyListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CurrencyList in the database
        List<CurrencyList> currencyListList = currencyListRepository.findAll();
        assertThat(currencyListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCurrencyList() throws Exception {
        int databaseSizeBeforeUpdate = currencyListRepository.findAll().size();
        currencyList.setId(UUID.randomUUID().toString());

        // Create the CurrencyList
        CurrencyListDTO currencyListDTO = currencyListMapper.toDto(currencyList);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCurrencyListMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(currencyListDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CurrencyList in the database
        List<CurrencyList> currencyListList = currencyListRepository.findAll();
        assertThat(currencyListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCurrencyList() throws Exception {
        // Initialize the database
        currencyList.setId(UUID.randomUUID().toString());
        currencyListRepository.saveAndFlush(currencyList);

        int databaseSizeBeforeDelete = currencyListRepository.findAll().size();

        // Delete the currencyList
        restCurrencyListMockMvc
            .perform(delete(ENTITY_API_URL_ID, currencyList.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CurrencyList> currencyListList = currencyListRepository.findAll();
        assertThat(currencyListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
