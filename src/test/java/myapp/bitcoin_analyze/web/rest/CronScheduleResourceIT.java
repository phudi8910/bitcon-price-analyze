package myapp.bitcoin_analyze.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import jakarta.persistence.EntityManager;
import myapp.bitcoin_analyze.IntegrationTest;
import myapp.bitcoin_analyze.domain.CronJob;
import myapp.bitcoin_analyze.domain.CronSchedule;
import myapp.bitcoin_analyze.repository.CronScheduleRepository;
import myapp.bitcoin_analyze.service.dto.CronScheduleDTO;
import myapp.bitcoin_analyze.service.mapper.CronScheduleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CronScheduleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CronScheduleResourceIT {

    private static final String DEFAULT_JOB_NAME = "AAAAAAAAAA";
    private static final String UPDATED_JOB_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CRON_EXPRESSION = "AAAAAAAAAA";
    private static final String UPDATED_CRON_EXPRESSION = "BBBBBBBBBB";

    private static final String DEFAULT_IS_ACTIVE = "AAAAAAAAAA";
    private static final String UPDATED_IS_ACTIVE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORD = 1;
    private static final Integer UPDATED_ORD = 2;
    private static final Integer SMALLER_ORD = 1 - 1;

    private static final String ENTITY_API_URL = "/api/cron-schedules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CronScheduleRepository cronScheduleRepository;

    @Autowired
    private CronScheduleMapper cronScheduleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCronScheduleMockMvc;

    private CronSchedule cronSchedule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CronSchedule createEntity(EntityManager em) {
        CronSchedule cronSchedule = new CronSchedule()
            .jobName(DEFAULT_JOB_NAME)
            .cronExpression(DEFAULT_CRON_EXPRESSION)
            .isActive(DEFAULT_IS_ACTIVE)
            .description(DEFAULT_DESCRIPTION)
            .ord(DEFAULT_ORD);
        return cronSchedule;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CronSchedule createUpdatedEntity(EntityManager em) {
        CronSchedule cronSchedule = new CronSchedule()
            .jobName(UPDATED_JOB_NAME)
            .cronExpression(UPDATED_CRON_EXPRESSION)
            .isActive(UPDATED_IS_ACTIVE)
            .description(UPDATED_DESCRIPTION)
            .ord(UPDATED_ORD);
        return cronSchedule;
    }

    @BeforeEach
    public void initTest() {
        cronSchedule = createEntity(em);
    }

    @Test
    @Transactional
    void createCronSchedule() throws Exception {
        int databaseSizeBeforeCreate = cronScheduleRepository.findAll().size();
        // Create the CronSchedule
        CronScheduleDTO cronScheduleDTO = cronScheduleMapper.toDto(cronSchedule);
        restCronScheduleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronScheduleDTO))
            )
            .andExpect(status().isCreated());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeCreate + 1);
        CronSchedule testCronSchedule = cronScheduleList.get(cronScheduleList.size() - 1);
        assertThat(testCronSchedule.getJobName()).isEqualTo(DEFAULT_JOB_NAME);
        assertThat(testCronSchedule.getCronExpression()).isEqualTo(DEFAULT_CRON_EXPRESSION);
        assertThat(testCronSchedule.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testCronSchedule.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCronSchedule.getOrd()).isEqualTo(DEFAULT_ORD);
    }

    @Test
    @Transactional
    void createCronScheduleWithExistingId() throws Exception {
        // Create the CronSchedule with an existing ID
        cronSchedule.setId(1L);
        CronScheduleDTO cronScheduleDTO = cronScheduleMapper.toDto(cronSchedule);

        int databaseSizeBeforeCreate = cronScheduleRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCronScheduleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCronSchedules() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList
        restCronScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cronSchedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].jobName").value(hasItem(DEFAULT_JOB_NAME)))
            .andExpect(jsonPath("$.[*].cronExpression").value(hasItem(DEFAULT_CRON_EXPRESSION)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].ord").value(hasItem(DEFAULT_ORD)));
    }

    @Test
    @Transactional
    void getCronSchedule() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get the cronSchedule
        restCronScheduleMockMvc
            .perform(get(ENTITY_API_URL_ID, cronSchedule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cronSchedule.getId().intValue()))
            .andExpect(jsonPath("$.jobName").value(DEFAULT_JOB_NAME))
            .andExpect(jsonPath("$.cronExpression").value(DEFAULT_CRON_EXPRESSION))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.ord").value(DEFAULT_ORD));
    }

    @Test
    @Transactional
    void getCronSchedulesByIdFiltering() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        Long id = cronSchedule.getId();

        defaultCronScheduleShouldBeFound("id.equals=" + id);
        defaultCronScheduleShouldNotBeFound("id.notEquals=" + id);

        defaultCronScheduleShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCronScheduleShouldNotBeFound("id.greaterThan=" + id);

        defaultCronScheduleShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCronScheduleShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByJobNameIsEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where jobName equals to DEFAULT_JOB_NAME
        defaultCronScheduleShouldBeFound("jobName.equals=" + DEFAULT_JOB_NAME);

        // Get all the cronScheduleList where jobName equals to UPDATED_JOB_NAME
        defaultCronScheduleShouldNotBeFound("jobName.equals=" + UPDATED_JOB_NAME);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByJobNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where jobName not equals to DEFAULT_JOB_NAME
        defaultCronScheduleShouldNotBeFound("jobName.notEquals=" + DEFAULT_JOB_NAME);

        // Get all the cronScheduleList where jobName not equals to UPDATED_JOB_NAME
        defaultCronScheduleShouldBeFound("jobName.notEquals=" + UPDATED_JOB_NAME);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByJobNameIsInShouldWork() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where jobName in DEFAULT_JOB_NAME or UPDATED_JOB_NAME
        defaultCronScheduleShouldBeFound("jobName.in=" + DEFAULT_JOB_NAME + "," + UPDATED_JOB_NAME);

        // Get all the cronScheduleList where jobName equals to UPDATED_JOB_NAME
        defaultCronScheduleShouldNotBeFound("jobName.in=" + UPDATED_JOB_NAME);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByJobNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where jobName is not null
        defaultCronScheduleShouldBeFound("jobName.specified=true");

        // Get all the cronScheduleList where jobName is null
        defaultCronScheduleShouldNotBeFound("jobName.specified=false");
    }

    @Test
    @Transactional
    void getAllCronSchedulesByJobNameContainsSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where jobName contains DEFAULT_JOB_NAME
        defaultCronScheduleShouldBeFound("jobName.contains=" + DEFAULT_JOB_NAME);

        // Get all the cronScheduleList where jobName contains UPDATED_JOB_NAME
        defaultCronScheduleShouldNotBeFound("jobName.contains=" + UPDATED_JOB_NAME);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByJobNameNotContainsSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where jobName does not contain DEFAULT_JOB_NAME
        defaultCronScheduleShouldNotBeFound("jobName.doesNotContain=" + DEFAULT_JOB_NAME);

        // Get all the cronScheduleList where jobName does not contain UPDATED_JOB_NAME
        defaultCronScheduleShouldBeFound("jobName.doesNotContain=" + UPDATED_JOB_NAME);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByCronExpressionIsEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where cronExpression equals to DEFAULT_CRON_EXPRESSION
        defaultCronScheduleShouldBeFound("cronExpression.equals=" + DEFAULT_CRON_EXPRESSION);

        // Get all the cronScheduleList where cronExpression equals to UPDATED_CRON_EXPRESSION
        defaultCronScheduleShouldNotBeFound("cronExpression.equals=" + UPDATED_CRON_EXPRESSION);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByCronExpressionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where cronExpression not equals to DEFAULT_CRON_EXPRESSION
        defaultCronScheduleShouldNotBeFound("cronExpression.notEquals=" + DEFAULT_CRON_EXPRESSION);

        // Get all the cronScheduleList where cronExpression not equals to UPDATED_CRON_EXPRESSION
        defaultCronScheduleShouldBeFound("cronExpression.notEquals=" + UPDATED_CRON_EXPRESSION);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByCronExpressionIsInShouldWork() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where cronExpression in DEFAULT_CRON_EXPRESSION or UPDATED_CRON_EXPRESSION
        defaultCronScheduleShouldBeFound("cronExpression.in=" + DEFAULT_CRON_EXPRESSION + "," + UPDATED_CRON_EXPRESSION);

        // Get all the cronScheduleList where cronExpression equals to UPDATED_CRON_EXPRESSION
        defaultCronScheduleShouldNotBeFound("cronExpression.in=" + UPDATED_CRON_EXPRESSION);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByCronExpressionIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where cronExpression is not null
        defaultCronScheduleShouldBeFound("cronExpression.specified=true");

        // Get all the cronScheduleList where cronExpression is null
        defaultCronScheduleShouldNotBeFound("cronExpression.specified=false");
    }

    @Test
    @Transactional
    void getAllCronSchedulesByCronExpressionContainsSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where cronExpression contains DEFAULT_CRON_EXPRESSION
        defaultCronScheduleShouldBeFound("cronExpression.contains=" + DEFAULT_CRON_EXPRESSION);

        // Get all the cronScheduleList where cronExpression contains UPDATED_CRON_EXPRESSION
        defaultCronScheduleShouldNotBeFound("cronExpression.contains=" + UPDATED_CRON_EXPRESSION);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByCronExpressionNotContainsSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where cronExpression does not contain DEFAULT_CRON_EXPRESSION
        defaultCronScheduleShouldNotBeFound("cronExpression.doesNotContain=" + DEFAULT_CRON_EXPRESSION);

        // Get all the cronScheduleList where cronExpression does not contain UPDATED_CRON_EXPRESSION
        defaultCronScheduleShouldBeFound("cronExpression.doesNotContain=" + UPDATED_CRON_EXPRESSION);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where isActive equals to DEFAULT_IS_ACTIVE
        defaultCronScheduleShouldBeFound("isActive.equals=" + DEFAULT_IS_ACTIVE);

        // Get all the cronScheduleList where isActive equals to UPDATED_IS_ACTIVE
        defaultCronScheduleShouldNotBeFound("isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByIsActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where isActive not equals to DEFAULT_IS_ACTIVE
        defaultCronScheduleShouldNotBeFound("isActive.notEquals=" + DEFAULT_IS_ACTIVE);

        // Get all the cronScheduleList where isActive not equals to UPDATED_IS_ACTIVE
        defaultCronScheduleShouldBeFound("isActive.notEquals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where isActive in DEFAULT_IS_ACTIVE or UPDATED_IS_ACTIVE
        defaultCronScheduleShouldBeFound("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE);

        // Get all the cronScheduleList where isActive equals to UPDATED_IS_ACTIVE
        defaultCronScheduleShouldNotBeFound("isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where isActive is not null
        defaultCronScheduleShouldBeFound("isActive.specified=true");

        // Get all the cronScheduleList where isActive is null
        defaultCronScheduleShouldNotBeFound("isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllCronSchedulesByIsActiveContainsSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where isActive contains DEFAULT_IS_ACTIVE
        defaultCronScheduleShouldBeFound("isActive.contains=" + DEFAULT_IS_ACTIVE);

        // Get all the cronScheduleList where isActive contains UPDATED_IS_ACTIVE
        defaultCronScheduleShouldNotBeFound("isActive.contains=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByIsActiveNotContainsSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where isActive does not contain DEFAULT_IS_ACTIVE
        defaultCronScheduleShouldNotBeFound("isActive.doesNotContain=" + DEFAULT_IS_ACTIVE);

        // Get all the cronScheduleList where isActive does not contain UPDATED_IS_ACTIVE
        defaultCronScheduleShouldBeFound("isActive.doesNotContain=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where description equals to DEFAULT_DESCRIPTION
        defaultCronScheduleShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the cronScheduleList where description equals to UPDATED_DESCRIPTION
        defaultCronScheduleShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where description not equals to DEFAULT_DESCRIPTION
        defaultCronScheduleShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the cronScheduleList where description not equals to UPDATED_DESCRIPTION
        defaultCronScheduleShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultCronScheduleShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the cronScheduleList where description equals to UPDATED_DESCRIPTION
        defaultCronScheduleShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where description is not null
        defaultCronScheduleShouldBeFound("description.specified=true");

        // Get all the cronScheduleList where description is null
        defaultCronScheduleShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllCronSchedulesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where description contains DEFAULT_DESCRIPTION
        defaultCronScheduleShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the cronScheduleList where description contains UPDATED_DESCRIPTION
        defaultCronScheduleShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where description does not contain DEFAULT_DESCRIPTION
        defaultCronScheduleShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the cronScheduleList where description does not contain UPDATED_DESCRIPTION
        defaultCronScheduleShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByOrdIsEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where ord equals to DEFAULT_ORD
        defaultCronScheduleShouldBeFound("ord.equals=" + DEFAULT_ORD);

        // Get all the cronScheduleList where ord equals to UPDATED_ORD
        defaultCronScheduleShouldNotBeFound("ord.equals=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByOrdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where ord not equals to DEFAULT_ORD
        defaultCronScheduleShouldNotBeFound("ord.notEquals=" + DEFAULT_ORD);

        // Get all the cronScheduleList where ord not equals to UPDATED_ORD
        defaultCronScheduleShouldBeFound("ord.notEquals=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByOrdIsInShouldWork() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where ord in DEFAULT_ORD or UPDATED_ORD
        defaultCronScheduleShouldBeFound("ord.in=" + DEFAULT_ORD + "," + UPDATED_ORD);

        // Get all the cronScheduleList where ord equals to UPDATED_ORD
        defaultCronScheduleShouldNotBeFound("ord.in=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByOrdIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where ord is not null
        defaultCronScheduleShouldBeFound("ord.specified=true");

        // Get all the cronScheduleList where ord is null
        defaultCronScheduleShouldNotBeFound("ord.specified=false");
    }

    @Test
    @Transactional
    void getAllCronSchedulesByOrdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where ord is greater than or equal to DEFAULT_ORD
        defaultCronScheduleShouldBeFound("ord.greaterThanOrEqual=" + DEFAULT_ORD);

        // Get all the cronScheduleList where ord is greater than or equal to UPDATED_ORD
        defaultCronScheduleShouldNotBeFound("ord.greaterThanOrEqual=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByOrdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where ord is less than or equal to DEFAULT_ORD
        defaultCronScheduleShouldBeFound("ord.lessThanOrEqual=" + DEFAULT_ORD);

        // Get all the cronScheduleList where ord is less than or equal to SMALLER_ORD
        defaultCronScheduleShouldNotBeFound("ord.lessThanOrEqual=" + SMALLER_ORD);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByOrdIsLessThanSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where ord is less than DEFAULT_ORD
        defaultCronScheduleShouldNotBeFound("ord.lessThan=" + DEFAULT_ORD);

        // Get all the cronScheduleList where ord is less than UPDATED_ORD
        defaultCronScheduleShouldBeFound("ord.lessThan=" + UPDATED_ORD);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByOrdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        // Get all the cronScheduleList where ord is greater than DEFAULT_ORD
        defaultCronScheduleShouldNotBeFound("ord.greaterThan=" + DEFAULT_ORD);

        // Get all the cronScheduleList where ord is greater than SMALLER_ORD
        defaultCronScheduleShouldBeFound("ord.greaterThan=" + SMALLER_ORD);
    }

    @Test
    @Transactional
    void getAllCronSchedulesByCronJobIsEqualToSomething() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);
        CronJob cronJob;
        if (TestUtil.findAll(em, CronJob.class).isEmpty()) {
            cronJob = CronJobResourceIT.createEntity(em);
            em.persist(cronJob);
            em.flush();
        } else {
            cronJob = TestUtil.findAll(em, CronJob.class).get(0);
        }
        em.persist(cronJob);
        em.flush();
        cronSchedule.addCronJob(cronJob);
        cronScheduleRepository.saveAndFlush(cronSchedule);
        Long cronJobId = cronJob.getId();

        // Get all the cronScheduleList where cronJob equals to cronJobId
        defaultCronScheduleShouldBeFound("cronJobId.equals=" + cronJobId);

        // Get all the cronScheduleList where cronJob equals to (cronJobId + 1)
        defaultCronScheduleShouldNotBeFound("cronJobId.equals=" + (cronJobId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCronScheduleShouldBeFound(String filter) throws Exception {
        restCronScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cronSchedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].jobName").value(hasItem(DEFAULT_JOB_NAME)))
            .andExpect(jsonPath("$.[*].cronExpression").value(hasItem(DEFAULT_CRON_EXPRESSION)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].ord").value(hasItem(DEFAULT_ORD)));

        // Check, that the count call also returns 1
        restCronScheduleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCronScheduleShouldNotBeFound(String filter) throws Exception {
        restCronScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCronScheduleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCronSchedule() throws Exception {
        // Get the cronSchedule
        restCronScheduleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCronSchedule() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        int databaseSizeBeforeUpdate = cronScheduleRepository.findAll().size();

        // Update the cronSchedule
        CronSchedule updatedCronSchedule = cronScheduleRepository.findById(cronSchedule.getId()).get();
        // Disconnect from session so that the updates on updatedCronSchedule are not directly saved in db
        em.detach(updatedCronSchedule);
        updatedCronSchedule
            .jobName(UPDATED_JOB_NAME)
            .cronExpression(UPDATED_CRON_EXPRESSION)
            .isActive(UPDATED_IS_ACTIVE)
            .description(UPDATED_DESCRIPTION)
            .ord(UPDATED_ORD);
        CronScheduleDTO cronScheduleDTO = cronScheduleMapper.toDto(updatedCronSchedule);

        restCronScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cronScheduleDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronScheduleDTO))
            )
            .andExpect(status().isOk());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeUpdate);
        CronSchedule testCronSchedule = cronScheduleList.get(cronScheduleList.size() - 1);
        assertThat(testCronSchedule.getJobName()).isEqualTo(UPDATED_JOB_NAME);
        assertThat(testCronSchedule.getCronExpression()).isEqualTo(UPDATED_CRON_EXPRESSION);
        assertThat(testCronSchedule.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testCronSchedule.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCronSchedule.getOrd()).isEqualTo(UPDATED_ORD);
    }

    @Test
    @Transactional
    void putNonExistingCronSchedule() throws Exception {
        int databaseSizeBeforeUpdate = cronScheduleRepository.findAll().size();
        cronSchedule.setId(count.incrementAndGet());

        // Create the CronSchedule
        CronScheduleDTO cronScheduleDTO = cronScheduleMapper.toDto(cronSchedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCronScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cronScheduleDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCronSchedule() throws Exception {
        int databaseSizeBeforeUpdate = cronScheduleRepository.findAll().size();
        cronSchedule.setId(count.incrementAndGet());

        // Create the CronSchedule
        CronScheduleDTO cronScheduleDTO = cronScheduleMapper.toDto(cronSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCronScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCronSchedule() throws Exception {
        int databaseSizeBeforeUpdate = cronScheduleRepository.findAll().size();
        cronSchedule.setId(count.incrementAndGet());

        // Create the CronSchedule
        CronScheduleDTO cronScheduleDTO = cronScheduleMapper.toDto(cronSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCronScheduleMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronScheduleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCronScheduleWithPatch() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        int databaseSizeBeforeUpdate = cronScheduleRepository.findAll().size();

        // Update the cronSchedule using partial update
        CronSchedule partialUpdatedCronSchedule = new CronSchedule();
        partialUpdatedCronSchedule.setId(cronSchedule.getId());

        partialUpdatedCronSchedule.jobName(UPDATED_JOB_NAME).isActive(UPDATED_IS_ACTIVE);

        restCronScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCronSchedule.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCronSchedule))
            )
            .andExpect(status().isOk());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeUpdate);
        CronSchedule testCronSchedule = cronScheduleList.get(cronScheduleList.size() - 1);
        assertThat(testCronSchedule.getJobName()).isEqualTo(UPDATED_JOB_NAME);
        assertThat(testCronSchedule.getCronExpression()).isEqualTo(DEFAULT_CRON_EXPRESSION);
        assertThat(testCronSchedule.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testCronSchedule.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCronSchedule.getOrd()).isEqualTo(DEFAULT_ORD);
    }

    @Test
    @Transactional
    void fullUpdateCronScheduleWithPatch() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        int databaseSizeBeforeUpdate = cronScheduleRepository.findAll().size();

        // Update the cronSchedule using partial update
        CronSchedule partialUpdatedCronSchedule = new CronSchedule();
        partialUpdatedCronSchedule.setId(cronSchedule.getId());

        partialUpdatedCronSchedule
            .jobName(UPDATED_JOB_NAME)
            .cronExpression(UPDATED_CRON_EXPRESSION)
            .isActive(UPDATED_IS_ACTIVE)
            .description(UPDATED_DESCRIPTION)
            .ord(UPDATED_ORD);

        restCronScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCronSchedule.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCronSchedule))
            )
            .andExpect(status().isOk());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeUpdate);
        CronSchedule testCronSchedule = cronScheduleList.get(cronScheduleList.size() - 1);
        assertThat(testCronSchedule.getJobName()).isEqualTo(UPDATED_JOB_NAME);
        assertThat(testCronSchedule.getCronExpression()).isEqualTo(UPDATED_CRON_EXPRESSION);
        assertThat(testCronSchedule.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testCronSchedule.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCronSchedule.getOrd()).isEqualTo(UPDATED_ORD);
    }

    @Test
    @Transactional
    void patchNonExistingCronSchedule() throws Exception {
        int databaseSizeBeforeUpdate = cronScheduleRepository.findAll().size();
        cronSchedule.setId(count.incrementAndGet());

        // Create the CronSchedule
        CronScheduleDTO cronScheduleDTO = cronScheduleMapper.toDto(cronSchedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCronScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cronScheduleDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cronScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCronSchedule() throws Exception {
        int databaseSizeBeforeUpdate = cronScheduleRepository.findAll().size();
        cronSchedule.setId(count.incrementAndGet());

        // Create the CronSchedule
        CronScheduleDTO cronScheduleDTO = cronScheduleMapper.toDto(cronSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCronScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cronScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCronSchedule() throws Exception {
        int databaseSizeBeforeUpdate = cronScheduleRepository.findAll().size();
        cronSchedule.setId(count.incrementAndGet());

        // Create the CronSchedule
        CronScheduleDTO cronScheduleDTO = cronScheduleMapper.toDto(cronSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCronScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cronScheduleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCronSchedule() throws Exception {
        // Initialize the database
        cronScheduleRepository.saveAndFlush(cronSchedule);

        int databaseSizeBeforeDelete = cronScheduleRepository.findAll().size();

        // Delete the cronSchedule
        restCronScheduleMockMvc
            .perform(delete(ENTITY_API_URL_ID, cronSchedule.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
