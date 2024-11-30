package myapp.bitcoin_analyze.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import jakarta.persistence.EntityManager;
import myapp.bitcoin_analyze.IntegrationTest;
import myapp.bitcoin_analyze.domain.CronJob;
import myapp.bitcoin_analyze.domain.CronSchedule;
import myapp.bitcoin_analyze.repository.CronJobRepository;
import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import myapp.bitcoin_analyze.service.mapper.CronJobMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CronJobResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CronJobResourceIT {

    private static final String DEFAULT_JOB_NAME = "AAAAAAAAAA";
    private static final String UPDATED_JOB_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CRON_EXPRESSION = "AAAAAAAAAA";
    private static final String UPDATED_CRON_EXPRESSION = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LOG_INFO = "AAAAAAAAAA";
    private static final String UPDATED_LOG_INFO = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cron-jobs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CronJobRepository cronJobRepository;

    @Autowired
    private CronJobMapper cronJobMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCronJobMockMvc;

    private CronJob cronJob;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CronJob createEntity(EntityManager em) {
        CronJob cronJob = new CronJob()
            .jobName(DEFAULT_JOB_NAME)
            .cronExpression(DEFAULT_CRON_EXPRESSION)
            .status(DEFAULT_STATUS)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .logInfo(DEFAULT_LOG_INFO)
            .description(DEFAULT_DESCRIPTION);
        return cronJob;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CronJob createUpdatedEntity(EntityManager em) {
        CronJob cronJob = new CronJob()
            .jobName(UPDATED_JOB_NAME)
            .cronExpression(UPDATED_CRON_EXPRESSION)
            .status(UPDATED_STATUS)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .logInfo(UPDATED_LOG_INFO)
            .description(UPDATED_DESCRIPTION);
        return cronJob;
    }

    @BeforeEach
    public void initTest() {
        cronJob = createEntity(em);
    }

    @Test
    @Transactional
    void createCronJob() throws Exception {
        int databaseSizeBeforeCreate = cronJobRepository.findAll().size();
        // Create the CronJob
        CronJobDTO cronJobDTO = cronJobMapper.toDto(cronJob);
        restCronJobMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronJobDTO))
            )
            .andExpect(status().isCreated());

        // Validate the CronJob in the database
        List<CronJob> cronJobList = cronJobRepository.findAll();
        assertThat(cronJobList).hasSize(databaseSizeBeforeCreate + 1);
        CronJob testCronJob = cronJobList.get(cronJobList.size() - 1);
        assertThat(testCronJob.getJobName()).isEqualTo(DEFAULT_JOB_NAME);
        assertThat(testCronJob.getCronExpression()).isEqualTo(DEFAULT_CRON_EXPRESSION);
        assertThat(testCronJob.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCronJob.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testCronJob.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testCronJob.getLogInfo()).isEqualTo(DEFAULT_LOG_INFO);
        assertThat(testCronJob.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createCronJobWithExistingId() throws Exception {
        // Create the CronJob with an existing ID
        cronJob.setId(1L);
        CronJobDTO cronJobDTO = cronJobMapper.toDto(cronJob);

        int databaseSizeBeforeCreate = cronJobRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCronJobMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronJob in the database
        List<CronJob> cronJobList = cronJobRepository.findAll();
        assertThat(cronJobList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCronJobs() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList
        restCronJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cronJob.getId().intValue())))
            .andExpect(jsonPath("$.[*].jobName").value(hasItem(DEFAULT_JOB_NAME)))
            .andExpect(jsonPath("$.[*].cronExpression").value(hasItem(DEFAULT_CRON_EXPRESSION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].logInfo").value(hasItem(DEFAULT_LOG_INFO)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getCronJob() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get the cronJob
        restCronJobMockMvc
            .perform(get(ENTITY_API_URL_ID, cronJob.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cronJob.getId().intValue()))
            .andExpect(jsonPath("$.jobName").value(DEFAULT_JOB_NAME))
            .andExpect(jsonPath("$.cronExpression").value(DEFAULT_CRON_EXPRESSION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.logInfo").value(DEFAULT_LOG_INFO))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getCronJobsByIdFiltering() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        Long id = cronJob.getId();

        defaultCronJobShouldBeFound("id.equals=" + id);
        defaultCronJobShouldNotBeFound("id.notEquals=" + id);

        defaultCronJobShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCronJobShouldNotBeFound("id.greaterThan=" + id);

        defaultCronJobShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCronJobShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCronJobsByJobNameIsEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where jobName equals to DEFAULT_JOB_NAME
        defaultCronJobShouldBeFound("jobName.equals=" + DEFAULT_JOB_NAME);

        // Get all the cronJobList where jobName equals to UPDATED_JOB_NAME
        defaultCronJobShouldNotBeFound("jobName.equals=" + UPDATED_JOB_NAME);
    }

    @Test
    @Transactional
    void getAllCronJobsByJobNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where jobName not equals to DEFAULT_JOB_NAME
        defaultCronJobShouldNotBeFound("jobName.notEquals=" + DEFAULT_JOB_NAME);

        // Get all the cronJobList where jobName not equals to UPDATED_JOB_NAME
        defaultCronJobShouldBeFound("jobName.notEquals=" + UPDATED_JOB_NAME);
    }

    @Test
    @Transactional
    void getAllCronJobsByJobNameIsInShouldWork() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where jobName in DEFAULT_JOB_NAME or UPDATED_JOB_NAME
        defaultCronJobShouldBeFound("jobName.in=" + DEFAULT_JOB_NAME + "," + UPDATED_JOB_NAME);

        // Get all the cronJobList where jobName equals to UPDATED_JOB_NAME
        defaultCronJobShouldNotBeFound("jobName.in=" + UPDATED_JOB_NAME);
    }

    @Test
    @Transactional
    void getAllCronJobsByJobNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where jobName is not null
        defaultCronJobShouldBeFound("jobName.specified=true");

        // Get all the cronJobList where jobName is null
        defaultCronJobShouldNotBeFound("jobName.specified=false");
    }

    @Test
    @Transactional
    void getAllCronJobsByJobNameContainsSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where jobName contains DEFAULT_JOB_NAME
        defaultCronJobShouldBeFound("jobName.contains=" + DEFAULT_JOB_NAME);

        // Get all the cronJobList where jobName contains UPDATED_JOB_NAME
        defaultCronJobShouldNotBeFound("jobName.contains=" + UPDATED_JOB_NAME);
    }

    @Test
    @Transactional
    void getAllCronJobsByJobNameNotContainsSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where jobName does not contain DEFAULT_JOB_NAME
        defaultCronJobShouldNotBeFound("jobName.doesNotContain=" + DEFAULT_JOB_NAME);

        // Get all the cronJobList where jobName does not contain UPDATED_JOB_NAME
        defaultCronJobShouldBeFound("jobName.doesNotContain=" + UPDATED_JOB_NAME);
    }

    @Test
    @Transactional
    void getAllCronJobsByCronExpressionIsEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where cronExpression equals to DEFAULT_CRON_EXPRESSION
        defaultCronJobShouldBeFound("cronExpression.equals=" + DEFAULT_CRON_EXPRESSION);

        // Get all the cronJobList where cronExpression equals to UPDATED_CRON_EXPRESSION
        defaultCronJobShouldNotBeFound("cronExpression.equals=" + UPDATED_CRON_EXPRESSION);
    }

    @Test
    @Transactional
    void getAllCronJobsByCronExpressionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where cronExpression not equals to DEFAULT_CRON_EXPRESSION
        defaultCronJobShouldNotBeFound("cronExpression.notEquals=" + DEFAULT_CRON_EXPRESSION);

        // Get all the cronJobList where cronExpression not equals to UPDATED_CRON_EXPRESSION
        defaultCronJobShouldBeFound("cronExpression.notEquals=" + UPDATED_CRON_EXPRESSION);
    }

    @Test
    @Transactional
    void getAllCronJobsByCronExpressionIsInShouldWork() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where cronExpression in DEFAULT_CRON_EXPRESSION or UPDATED_CRON_EXPRESSION
        defaultCronJobShouldBeFound("cronExpression.in=" + DEFAULT_CRON_EXPRESSION + "," + UPDATED_CRON_EXPRESSION);

        // Get all the cronJobList where cronExpression equals to UPDATED_CRON_EXPRESSION
        defaultCronJobShouldNotBeFound("cronExpression.in=" + UPDATED_CRON_EXPRESSION);
    }

    @Test
    @Transactional
    void getAllCronJobsByCronExpressionIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where cronExpression is not null
        defaultCronJobShouldBeFound("cronExpression.specified=true");

        // Get all the cronJobList where cronExpression is null
        defaultCronJobShouldNotBeFound("cronExpression.specified=false");
    }

    @Test
    @Transactional
    void getAllCronJobsByCronExpressionContainsSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where cronExpression contains DEFAULT_CRON_EXPRESSION
        defaultCronJobShouldBeFound("cronExpression.contains=" + DEFAULT_CRON_EXPRESSION);

        // Get all the cronJobList where cronExpression contains UPDATED_CRON_EXPRESSION
        defaultCronJobShouldNotBeFound("cronExpression.contains=" + UPDATED_CRON_EXPRESSION);
    }

    @Test
    @Transactional
    void getAllCronJobsByCronExpressionNotContainsSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where cronExpression does not contain DEFAULT_CRON_EXPRESSION
        defaultCronJobShouldNotBeFound("cronExpression.doesNotContain=" + DEFAULT_CRON_EXPRESSION);

        // Get all the cronJobList where cronExpression does not contain UPDATED_CRON_EXPRESSION
        defaultCronJobShouldBeFound("cronExpression.doesNotContain=" + UPDATED_CRON_EXPRESSION);
    }

    @Test
    @Transactional
    void getAllCronJobsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where status equals to DEFAULT_STATUS
        defaultCronJobShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the cronJobList where status equals to UPDATED_STATUS
        defaultCronJobShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCronJobsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where status not equals to DEFAULT_STATUS
        defaultCronJobShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the cronJobList where status not equals to UPDATED_STATUS
        defaultCronJobShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCronJobsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultCronJobShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the cronJobList where status equals to UPDATED_STATUS
        defaultCronJobShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCronJobsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where status is not null
        defaultCronJobShouldBeFound("status.specified=true");

        // Get all the cronJobList where status is null
        defaultCronJobShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllCronJobsByStatusContainsSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where status contains DEFAULT_STATUS
        defaultCronJobShouldBeFound("status.contains=" + DEFAULT_STATUS);

        // Get all the cronJobList where status contains UPDATED_STATUS
        defaultCronJobShouldNotBeFound("status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCronJobsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where status does not contain DEFAULT_STATUS
        defaultCronJobShouldNotBeFound("status.doesNotContain=" + DEFAULT_STATUS);

        // Get all the cronJobList where status does not contain UPDATED_STATUS
        defaultCronJobShouldBeFound("status.doesNotContain=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCronJobsByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where startTime equals to DEFAULT_START_TIME
        defaultCronJobShouldBeFound("startTime.equals=" + DEFAULT_START_TIME);

        // Get all the cronJobList where startTime equals to UPDATED_START_TIME
        defaultCronJobShouldNotBeFound("startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllCronJobsByStartTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where startTime not equals to DEFAULT_START_TIME
        defaultCronJobShouldNotBeFound("startTime.notEquals=" + DEFAULT_START_TIME);

        // Get all the cronJobList where startTime not equals to UPDATED_START_TIME
        defaultCronJobShouldBeFound("startTime.notEquals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllCronJobsByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where startTime in DEFAULT_START_TIME or UPDATED_START_TIME
        defaultCronJobShouldBeFound("startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME);

        // Get all the cronJobList where startTime equals to UPDATED_START_TIME
        defaultCronJobShouldNotBeFound("startTime.in=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllCronJobsByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where startTime is not null
        defaultCronJobShouldBeFound("startTime.specified=true");

        // Get all the cronJobList where startTime is null
        defaultCronJobShouldNotBeFound("startTime.specified=false");
    }

    @Test
    @Transactional
    void getAllCronJobsByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where endTime equals to DEFAULT_END_TIME
        defaultCronJobShouldBeFound("endTime.equals=" + DEFAULT_END_TIME);

        // Get all the cronJobList where endTime equals to UPDATED_END_TIME
        defaultCronJobShouldNotBeFound("endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllCronJobsByEndTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where endTime not equals to DEFAULT_END_TIME
        defaultCronJobShouldNotBeFound("endTime.notEquals=" + DEFAULT_END_TIME);

        // Get all the cronJobList where endTime not equals to UPDATED_END_TIME
        defaultCronJobShouldBeFound("endTime.notEquals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllCronJobsByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where endTime in DEFAULT_END_TIME or UPDATED_END_TIME
        defaultCronJobShouldBeFound("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME);

        // Get all the cronJobList where endTime equals to UPDATED_END_TIME
        defaultCronJobShouldNotBeFound("endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllCronJobsByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where endTime is not null
        defaultCronJobShouldBeFound("endTime.specified=true");

        // Get all the cronJobList where endTime is null
        defaultCronJobShouldNotBeFound("endTime.specified=false");
    }

    @Test
    @Transactional
    void getAllCronJobsByLogInfoIsEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where logInfo equals to DEFAULT_LOG_INFO
        defaultCronJobShouldBeFound("logInfo.equals=" + DEFAULT_LOG_INFO);

        // Get all the cronJobList where logInfo equals to UPDATED_LOG_INFO
        defaultCronJobShouldNotBeFound("logInfo.equals=" + UPDATED_LOG_INFO);
    }

    @Test
    @Transactional
    void getAllCronJobsByLogInfoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where logInfo not equals to DEFAULT_LOG_INFO
        defaultCronJobShouldNotBeFound("logInfo.notEquals=" + DEFAULT_LOG_INFO);

        // Get all the cronJobList where logInfo not equals to UPDATED_LOG_INFO
        defaultCronJobShouldBeFound("logInfo.notEquals=" + UPDATED_LOG_INFO);
    }

    @Test
    @Transactional
    void getAllCronJobsByLogInfoIsInShouldWork() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where logInfo in DEFAULT_LOG_INFO or UPDATED_LOG_INFO
        defaultCronJobShouldBeFound("logInfo.in=" + DEFAULT_LOG_INFO + "," + UPDATED_LOG_INFO);

        // Get all the cronJobList where logInfo equals to UPDATED_LOG_INFO
        defaultCronJobShouldNotBeFound("logInfo.in=" + UPDATED_LOG_INFO);
    }

    @Test
    @Transactional
    void getAllCronJobsByLogInfoIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where logInfo is not null
        defaultCronJobShouldBeFound("logInfo.specified=true");

        // Get all the cronJobList where logInfo is null
        defaultCronJobShouldNotBeFound("logInfo.specified=false");
    }

    @Test
    @Transactional
    void getAllCronJobsByLogInfoContainsSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where logInfo contains DEFAULT_LOG_INFO
        defaultCronJobShouldBeFound("logInfo.contains=" + DEFAULT_LOG_INFO);

        // Get all the cronJobList where logInfo contains UPDATED_LOG_INFO
        defaultCronJobShouldNotBeFound("logInfo.contains=" + UPDATED_LOG_INFO);
    }

    @Test
    @Transactional
    void getAllCronJobsByLogInfoNotContainsSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where logInfo does not contain DEFAULT_LOG_INFO
        defaultCronJobShouldNotBeFound("logInfo.doesNotContain=" + DEFAULT_LOG_INFO);

        // Get all the cronJobList where logInfo does not contain UPDATED_LOG_INFO
        defaultCronJobShouldBeFound("logInfo.doesNotContain=" + UPDATED_LOG_INFO);
    }

    @Test
    @Transactional
    void getAllCronJobsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where description equals to DEFAULT_DESCRIPTION
        defaultCronJobShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the cronJobList where description equals to UPDATED_DESCRIPTION
        defaultCronJobShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCronJobsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where description not equals to DEFAULT_DESCRIPTION
        defaultCronJobShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the cronJobList where description not equals to UPDATED_DESCRIPTION
        defaultCronJobShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCronJobsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultCronJobShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the cronJobList where description equals to UPDATED_DESCRIPTION
        defaultCronJobShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCronJobsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where description is not null
        defaultCronJobShouldBeFound("description.specified=true");

        // Get all the cronJobList where description is null
        defaultCronJobShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllCronJobsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where description contains DEFAULT_DESCRIPTION
        defaultCronJobShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the cronJobList where description contains UPDATED_DESCRIPTION
        defaultCronJobShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCronJobsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        // Get all the cronJobList where description does not contain DEFAULT_DESCRIPTION
        defaultCronJobShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the cronJobList where description does not contain UPDATED_DESCRIPTION
        defaultCronJobShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCronJobsByCronScheduleIsEqualToSomething() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);
        CronSchedule cronSchedule;
        if (TestUtil.findAll(em, CronSchedule.class).isEmpty()) {
            cronSchedule = CronScheduleResourceIT.createEntity(em);
            em.persist(cronSchedule);
            em.flush();
        } else {
            cronSchedule = TestUtil.findAll(em, CronSchedule.class).get(0);
        }
        em.persist(cronSchedule);
        em.flush();
        cronJob.setCronSchedule(cronSchedule);
        cronJobRepository.saveAndFlush(cronJob);
        Long cronScheduleId = cronSchedule.getId();

        // Get all the cronJobList where cronSchedule equals to cronScheduleId
        defaultCronJobShouldBeFound("cronScheduleId.equals=" + cronScheduleId);

        // Get all the cronJobList where cronSchedule equals to (cronScheduleId + 1)
        defaultCronJobShouldNotBeFound("cronScheduleId.equals=" + (cronScheduleId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCronJobShouldBeFound(String filter) throws Exception {
        restCronJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cronJob.getId().intValue())))
            .andExpect(jsonPath("$.[*].jobName").value(hasItem(DEFAULT_JOB_NAME)))
            .andExpect(jsonPath("$.[*].cronExpression").value(hasItem(DEFAULT_CRON_EXPRESSION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].logInfo").value(hasItem(DEFAULT_LOG_INFO)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restCronJobMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCronJobShouldNotBeFound(String filter) throws Exception {
        restCronJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCronJobMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCronJob() throws Exception {
        // Get the cronJob
        restCronJobMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCronJob() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        int databaseSizeBeforeUpdate = cronJobRepository.findAll().size();

        // Update the cronJob
        CronJob updatedCronJob = cronJobRepository.findById(cronJob.getId()).get();
        // Disconnect from session so that the updates on updatedCronJob are not directly saved in db
        em.detach(updatedCronJob);
        updatedCronJob
            .jobName(UPDATED_JOB_NAME)
            .cronExpression(UPDATED_CRON_EXPRESSION)
            .status(UPDATED_STATUS)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .logInfo(UPDATED_LOG_INFO)
            .description(UPDATED_DESCRIPTION);
        CronJobDTO cronJobDTO = cronJobMapper.toDto(updatedCronJob);

        restCronJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cronJobDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronJobDTO))
            )
            .andExpect(status().isOk());

        // Validate the CronJob in the database
        List<CronJob> cronJobList = cronJobRepository.findAll();
        assertThat(cronJobList).hasSize(databaseSizeBeforeUpdate);
        CronJob testCronJob = cronJobList.get(cronJobList.size() - 1);
        assertThat(testCronJob.getJobName()).isEqualTo(UPDATED_JOB_NAME);
        assertThat(testCronJob.getCronExpression()).isEqualTo(UPDATED_CRON_EXPRESSION);
        assertThat(testCronJob.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCronJob.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testCronJob.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testCronJob.getLogInfo()).isEqualTo(UPDATED_LOG_INFO);
        assertThat(testCronJob.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingCronJob() throws Exception {
        int databaseSizeBeforeUpdate = cronJobRepository.findAll().size();
        cronJob.setId(count.incrementAndGet());

        // Create the CronJob
        CronJobDTO cronJobDTO = cronJobMapper.toDto(cronJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCronJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cronJobDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronJob in the database
        List<CronJob> cronJobList = cronJobRepository.findAll();
        assertThat(cronJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCronJob() throws Exception {
        int databaseSizeBeforeUpdate = cronJobRepository.findAll().size();
        cronJob.setId(count.incrementAndGet());

        // Create the CronJob
        CronJobDTO cronJobDTO = cronJobMapper.toDto(cronJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCronJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronJob in the database
        List<CronJob> cronJobList = cronJobRepository.findAll();
        assertThat(cronJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCronJob() throws Exception {
        int databaseSizeBeforeUpdate = cronJobRepository.findAll().size();
        cronJob.setId(count.incrementAndGet());

        // Create the CronJob
        CronJobDTO cronJobDTO = cronJobMapper.toDto(cronJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCronJobMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronJobDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CronJob in the database
        List<CronJob> cronJobList = cronJobRepository.findAll();
        assertThat(cronJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCronJobWithPatch() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        int databaseSizeBeforeUpdate = cronJobRepository.findAll().size();

        // Update the cronJob using partial update
        CronJob partialUpdatedCronJob = new CronJob();
        partialUpdatedCronJob.setId(cronJob.getId());

        partialUpdatedCronJob
            .cronExpression(UPDATED_CRON_EXPRESSION)
            .status(UPDATED_STATUS)
            .endTime(UPDATED_END_TIME)
            .logInfo(UPDATED_LOG_INFO);

        restCronJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCronJob.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCronJob))
            )
            .andExpect(status().isOk());

        // Validate the CronJob in the database
        List<CronJob> cronJobList = cronJobRepository.findAll();
        assertThat(cronJobList).hasSize(databaseSizeBeforeUpdate);
        CronJob testCronJob = cronJobList.get(cronJobList.size() - 1);
        assertThat(testCronJob.getJobName()).isEqualTo(DEFAULT_JOB_NAME);
        assertThat(testCronJob.getCronExpression()).isEqualTo(UPDATED_CRON_EXPRESSION);
        assertThat(testCronJob.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCronJob.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testCronJob.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testCronJob.getLogInfo()).isEqualTo(UPDATED_LOG_INFO);
        assertThat(testCronJob.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateCronJobWithPatch() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        int databaseSizeBeforeUpdate = cronJobRepository.findAll().size();

        // Update the cronJob using partial update
        CronJob partialUpdatedCronJob = new CronJob();
        partialUpdatedCronJob.setId(cronJob.getId());

        partialUpdatedCronJob
            .jobName(UPDATED_JOB_NAME)
            .cronExpression(UPDATED_CRON_EXPRESSION)
            .status(UPDATED_STATUS)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .logInfo(UPDATED_LOG_INFO)
            .description(UPDATED_DESCRIPTION);

        restCronJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCronJob.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCronJob))
            )
            .andExpect(status().isOk());

        // Validate the CronJob in the database
        List<CronJob> cronJobList = cronJobRepository.findAll();
        assertThat(cronJobList).hasSize(databaseSizeBeforeUpdate);
        CronJob testCronJob = cronJobList.get(cronJobList.size() - 1);
        assertThat(testCronJob.getJobName()).isEqualTo(UPDATED_JOB_NAME);
        assertThat(testCronJob.getCronExpression()).isEqualTo(UPDATED_CRON_EXPRESSION);
        assertThat(testCronJob.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCronJob.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testCronJob.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testCronJob.getLogInfo()).isEqualTo(UPDATED_LOG_INFO);
        assertThat(testCronJob.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingCronJob() throws Exception {
        int databaseSizeBeforeUpdate = cronJobRepository.findAll().size();
        cronJob.setId(count.incrementAndGet());

        // Create the CronJob
        CronJobDTO cronJobDTO = cronJobMapper.toDto(cronJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCronJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cronJobDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cronJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronJob in the database
        List<CronJob> cronJobList = cronJobRepository.findAll();
        assertThat(cronJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCronJob() throws Exception {
        int databaseSizeBeforeUpdate = cronJobRepository.findAll().size();
        cronJob.setId(count.incrementAndGet());

        // Create the CronJob
        CronJobDTO cronJobDTO = cronJobMapper.toDto(cronJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCronJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cronJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronJob in the database
        List<CronJob> cronJobList = cronJobRepository.findAll();
        assertThat(cronJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCronJob() throws Exception {
        int databaseSizeBeforeUpdate = cronJobRepository.findAll().size();
        cronJob.setId(count.incrementAndGet());

        // Create the CronJob
        CronJobDTO cronJobDTO = cronJobMapper.toDto(cronJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCronJobMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cronJobDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CronJob in the database
        List<CronJob> cronJobList = cronJobRepository.findAll();
        assertThat(cronJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCronJob() throws Exception {
        // Initialize the database
        cronJobRepository.saveAndFlush(cronJob);

        int databaseSizeBeforeDelete = cronJobRepository.findAll().size();

        // Delete the cronJob
        restCronJobMockMvc
            .perform(delete(ENTITY_API_URL_ID, cronJob.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CronJob> cronJobList = cronJobRepository.findAll();
        assertThat(cronJobList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
