package myapp.bitcoin_analyze.web.rest;

import static myapp.bitcoin_analyze.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import jakarta.persistence.EntityManager;
import myapp.bitcoin_analyze.IntegrationTest;
import myapp.bitcoin_analyze.domain.BitcoinAnalyze;
import myapp.bitcoin_analyze.repository.BitcoinAnalyzeRepository;
import myapp.bitcoin_analyze.service.dto.BitcoinAnalyzeDTO;
import myapp.bitcoin_analyze.service.mapper.BitcoinAnalyzeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BitcoinAnalyzeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BitcoinAnalyzeResourceIT {

    private static final String DEFAULT_MARKET_API_ID = "AAAAAAAAAA";
    private static final String UPDATED_MARKET_API_ID = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_OPEN_RATE_FLOAT = new BigDecimal(1);
    private static final BigDecimal UPDATED_OPEN_RATE_FLOAT = new BigDecimal(2);
    private static final BigDecimal SMALLER_OPEN_RATE_FLOAT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_CLOSED_RATE_FLOAT = new BigDecimal(1);
    private static final BigDecimal UPDATED_CLOSED_RATE_FLOAT = new BigDecimal(2);
    private static final BigDecimal SMALLER_CLOSED_RATE_FLOAT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_RATIO_OPEN_CLOSED_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_RATIO_OPEN_CLOSED_RATE = new BigDecimal(2);
    private static final BigDecimal SMALLER_RATIO_OPEN_CLOSED_RATE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_MAX_RATE_FLOAT = new BigDecimal(1);
    private static final BigDecimal UPDATED_MAX_RATE_FLOAT = new BigDecimal(2);
    private static final BigDecimal SMALLER_MAX_RATE_FLOAT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_MIN_RATE_FLOAT = new BigDecimal(1);
    private static final BigDecimal UPDATED_MIN_RATE_FLOAT = new BigDecimal(2);
    private static final BigDecimal SMALLER_MIN_RATE_FLOAT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_RATIO_RATE_FLOAT = new BigDecimal(1);
    private static final BigDecimal UPDATED_RATIO_RATE_FLOAT = new BigDecimal(2);
    private static final BigDecimal SMALLER_RATIO_RATE_FLOAT = new BigDecimal(1 - 1);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATED_DATE = LocalDate.ofEpochDay(-1L);

    private static final Instant DEFAULT_CREATED_TIME_STAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_TIME_STAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_TIME_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TIME_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/bitcoin-analyzes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BitcoinAnalyzeRepository bitcoinAnalyzeRepository;

    @Autowired
    private BitcoinAnalyzeMapper bitcoinAnalyzeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBitcoinAnalyzeMockMvc;

    private BitcoinAnalyze bitcoinAnalyze;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BitcoinAnalyze createEntity(EntityManager em) {
        BitcoinAnalyze bitcoinAnalyze = new BitcoinAnalyze()
            .marketApiId(DEFAULT_MARKET_API_ID)
            .currencyCode(DEFAULT_CURRENCY_CODE)
            .symbol(DEFAULT_SYMBOL)
            .openRateFloat(DEFAULT_OPEN_RATE_FLOAT)
            .closedRateFloat(DEFAULT_CLOSED_RATE_FLOAT)
            .ratioOpenClosedRate(DEFAULT_RATIO_OPEN_CLOSED_RATE)
            .maxRateFloat(DEFAULT_MAX_RATE_FLOAT)
            .minRateFloat(DEFAULT_MIN_RATE_FLOAT)
            .ratioRateFloat(DEFAULT_RATIO_RATE_FLOAT)
            .description(DEFAULT_DESCRIPTION)
            .createdDate(DEFAULT_CREATED_DATE)
            .createdTimeStamp(DEFAULT_CREATED_TIME_STAMP)
            .timeType(DEFAULT_TIME_TYPE);
        return bitcoinAnalyze;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BitcoinAnalyze createUpdatedEntity(EntityManager em) {
        BitcoinAnalyze bitcoinAnalyze = new BitcoinAnalyze()
            .marketApiId(UPDATED_MARKET_API_ID)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .symbol(UPDATED_SYMBOL)
            .openRateFloat(UPDATED_OPEN_RATE_FLOAT)
            .closedRateFloat(UPDATED_CLOSED_RATE_FLOAT)
            .ratioOpenClosedRate(UPDATED_RATIO_OPEN_CLOSED_RATE)
            .maxRateFloat(UPDATED_MAX_RATE_FLOAT)
            .minRateFloat(UPDATED_MIN_RATE_FLOAT)
            .ratioRateFloat(UPDATED_RATIO_RATE_FLOAT)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .createdTimeStamp(UPDATED_CREATED_TIME_STAMP)
            .timeType(UPDATED_TIME_TYPE);
        return bitcoinAnalyze;
    }

    @BeforeEach
    public void initTest() {
        bitcoinAnalyze = createEntity(em);
    }

    @Test
    @Transactional
    void createBitcoinAnalyze() throws Exception {
        int databaseSizeBeforeCreate = bitcoinAnalyzeRepository.findAll().size();
        // Create the BitcoinAnalyze
        BitcoinAnalyzeDTO bitcoinAnalyzeDTO = bitcoinAnalyzeMapper.toDto(bitcoinAnalyze);
        restBitcoinAnalyzeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bitcoinAnalyzeDTO))
            )
            .andExpect(status().isCreated());

        // Validate the BitcoinAnalyze in the database
        List<BitcoinAnalyze> bitcoinAnalyzeList = bitcoinAnalyzeRepository.findAll();
        assertThat(bitcoinAnalyzeList).hasSize(databaseSizeBeforeCreate + 1);
        BitcoinAnalyze testBitcoinAnalyze = bitcoinAnalyzeList.get(bitcoinAnalyzeList.size() - 1);
        assertThat(testBitcoinAnalyze.getMarketApiId()).isEqualTo(DEFAULT_MARKET_API_ID);
        assertThat(testBitcoinAnalyze.getCurrencyCode()).isEqualTo(DEFAULT_CURRENCY_CODE);
        assertThat(testBitcoinAnalyze.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testBitcoinAnalyze.getOpenRateFloat()).isEqualByComparingTo(DEFAULT_OPEN_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getClosedRateFloat()).isEqualByComparingTo(DEFAULT_CLOSED_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getRatioOpenClosedRate()).isEqualByComparingTo(DEFAULT_RATIO_OPEN_CLOSED_RATE);
        assertThat(testBitcoinAnalyze.getMaxRateFloat()).isEqualByComparingTo(DEFAULT_MAX_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getMinRateFloat()).isEqualByComparingTo(DEFAULT_MIN_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getRatioRateFloat()).isEqualByComparingTo(DEFAULT_RATIO_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBitcoinAnalyze.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testBitcoinAnalyze.getCreatedTimeStamp()).isEqualTo(DEFAULT_CREATED_TIME_STAMP);
        assertThat(testBitcoinAnalyze.getTimeType()).isEqualTo(DEFAULT_TIME_TYPE);
    }

    @Test
    @Transactional
    void createBitcoinAnalyzeWithExistingId() throws Exception {
        // Create the BitcoinAnalyze with an existing ID
        bitcoinAnalyze.setId(1L);
        BitcoinAnalyzeDTO bitcoinAnalyzeDTO = bitcoinAnalyzeMapper.toDto(bitcoinAnalyze);

        int databaseSizeBeforeCreate = bitcoinAnalyzeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBitcoinAnalyzeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bitcoinAnalyzeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BitcoinAnalyze in the database
        List<BitcoinAnalyze> bitcoinAnalyzeList = bitcoinAnalyzeRepository.findAll();
        assertThat(bitcoinAnalyzeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzes() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList
        restBitcoinAnalyzeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bitcoinAnalyze.getId().intValue())))
            .andExpect(jsonPath("$.[*].marketApiId").value(hasItem(DEFAULT_MARKET_API_ID)))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].openRateFloat").value(hasItem(sameNumber(DEFAULT_OPEN_RATE_FLOAT))))
            .andExpect(jsonPath("$.[*].closedRateFloat").value(hasItem(sameNumber(DEFAULT_CLOSED_RATE_FLOAT))))
            .andExpect(jsonPath("$.[*].ratioOpenClosedRate").value(hasItem(sameNumber(DEFAULT_RATIO_OPEN_CLOSED_RATE))))
            .andExpect(jsonPath("$.[*].maxRateFloat").value(hasItem(sameNumber(DEFAULT_MAX_RATE_FLOAT))))
            .andExpect(jsonPath("$.[*].minRateFloat").value(hasItem(sameNumber(DEFAULT_MIN_RATE_FLOAT))))
            .andExpect(jsonPath("$.[*].ratioRateFloat").value(hasItem(sameNumber(DEFAULT_RATIO_RATE_FLOAT))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdTimeStamp").value(hasItem(DEFAULT_CREATED_TIME_STAMP.toString())))
            .andExpect(jsonPath("$.[*].timeType").value(hasItem(DEFAULT_TIME_TYPE)));
    }

    @Test
    @Transactional
    void getBitcoinAnalyze() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get the bitcoinAnalyze
        restBitcoinAnalyzeMockMvc
            .perform(get(ENTITY_API_URL_ID, bitcoinAnalyze.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bitcoinAnalyze.getId().intValue()))
            .andExpect(jsonPath("$.marketApiId").value(DEFAULT_MARKET_API_ID))
            .andExpect(jsonPath("$.currencyCode").value(DEFAULT_CURRENCY_CODE))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL))
            .andExpect(jsonPath("$.openRateFloat").value(sameNumber(DEFAULT_OPEN_RATE_FLOAT)))
            .andExpect(jsonPath("$.closedRateFloat").value(sameNumber(DEFAULT_CLOSED_RATE_FLOAT)))
            .andExpect(jsonPath("$.ratioOpenClosedRate").value(sameNumber(DEFAULT_RATIO_OPEN_CLOSED_RATE)))
            .andExpect(jsonPath("$.maxRateFloat").value(sameNumber(DEFAULT_MAX_RATE_FLOAT)))
            .andExpect(jsonPath("$.minRateFloat").value(sameNumber(DEFAULT_MIN_RATE_FLOAT)))
            .andExpect(jsonPath("$.ratioRateFloat").value(sameNumber(DEFAULT_RATIO_RATE_FLOAT)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.createdTimeStamp").value(DEFAULT_CREATED_TIME_STAMP.toString()))
            .andExpect(jsonPath("$.timeType").value(DEFAULT_TIME_TYPE));
    }

    @Test
    @Transactional
    void getBitcoinAnalyzesByIdFiltering() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        Long id = bitcoinAnalyze.getId();

        defaultBitcoinAnalyzeShouldBeFound("id.equals=" + id);
        defaultBitcoinAnalyzeShouldNotBeFound("id.notEquals=" + id);

        defaultBitcoinAnalyzeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBitcoinAnalyzeShouldNotBeFound("id.greaterThan=" + id);

        defaultBitcoinAnalyzeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBitcoinAnalyzeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMarketApiIdIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where marketApiId equals to DEFAULT_MARKET_API_ID
        defaultBitcoinAnalyzeShouldBeFound("marketApiId.equals=" + DEFAULT_MARKET_API_ID);

        // Get all the bitcoinAnalyzeList where marketApiId equals to UPDATED_MARKET_API_ID
        defaultBitcoinAnalyzeShouldNotBeFound("marketApiId.equals=" + UPDATED_MARKET_API_ID);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMarketApiIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where marketApiId not equals to DEFAULT_MARKET_API_ID
        defaultBitcoinAnalyzeShouldNotBeFound("marketApiId.notEquals=" + DEFAULT_MARKET_API_ID);

        // Get all the bitcoinAnalyzeList where marketApiId not equals to UPDATED_MARKET_API_ID
        defaultBitcoinAnalyzeShouldBeFound("marketApiId.notEquals=" + UPDATED_MARKET_API_ID);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMarketApiIdIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where marketApiId in DEFAULT_MARKET_API_ID or UPDATED_MARKET_API_ID
        defaultBitcoinAnalyzeShouldBeFound("marketApiId.in=" + DEFAULT_MARKET_API_ID + "," + UPDATED_MARKET_API_ID);

        // Get all the bitcoinAnalyzeList where marketApiId equals to UPDATED_MARKET_API_ID
        defaultBitcoinAnalyzeShouldNotBeFound("marketApiId.in=" + UPDATED_MARKET_API_ID);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMarketApiIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where marketApiId is not null
        defaultBitcoinAnalyzeShouldBeFound("marketApiId.specified=true");

        // Get all the bitcoinAnalyzeList where marketApiId is null
        defaultBitcoinAnalyzeShouldNotBeFound("marketApiId.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMarketApiIdContainsSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where marketApiId contains DEFAULT_MARKET_API_ID
        defaultBitcoinAnalyzeShouldBeFound("marketApiId.contains=" + DEFAULT_MARKET_API_ID);

        // Get all the bitcoinAnalyzeList where marketApiId contains UPDATED_MARKET_API_ID
        defaultBitcoinAnalyzeShouldNotBeFound("marketApiId.contains=" + UPDATED_MARKET_API_ID);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMarketApiIdNotContainsSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where marketApiId does not contain DEFAULT_MARKET_API_ID
        defaultBitcoinAnalyzeShouldNotBeFound("marketApiId.doesNotContain=" + DEFAULT_MARKET_API_ID);

        // Get all the bitcoinAnalyzeList where marketApiId does not contain UPDATED_MARKET_API_ID
        defaultBitcoinAnalyzeShouldBeFound("marketApiId.doesNotContain=" + UPDATED_MARKET_API_ID);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCurrencyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where currencyCode equals to DEFAULT_CURRENCY_CODE
        defaultBitcoinAnalyzeShouldBeFound("currencyCode.equals=" + DEFAULT_CURRENCY_CODE);

        // Get all the bitcoinAnalyzeList where currencyCode equals to UPDATED_CURRENCY_CODE
        defaultBitcoinAnalyzeShouldNotBeFound("currencyCode.equals=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCurrencyCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where currencyCode not equals to DEFAULT_CURRENCY_CODE
        defaultBitcoinAnalyzeShouldNotBeFound("currencyCode.notEquals=" + DEFAULT_CURRENCY_CODE);

        // Get all the bitcoinAnalyzeList where currencyCode not equals to UPDATED_CURRENCY_CODE
        defaultBitcoinAnalyzeShouldBeFound("currencyCode.notEquals=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCurrencyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where currencyCode in DEFAULT_CURRENCY_CODE or UPDATED_CURRENCY_CODE
        defaultBitcoinAnalyzeShouldBeFound("currencyCode.in=" + DEFAULT_CURRENCY_CODE + "," + UPDATED_CURRENCY_CODE);

        // Get all the bitcoinAnalyzeList where currencyCode equals to UPDATED_CURRENCY_CODE
        defaultBitcoinAnalyzeShouldNotBeFound("currencyCode.in=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCurrencyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where currencyCode is not null
        defaultBitcoinAnalyzeShouldBeFound("currencyCode.specified=true");

        // Get all the bitcoinAnalyzeList where currencyCode is null
        defaultBitcoinAnalyzeShouldNotBeFound("currencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCurrencyCodeContainsSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where currencyCode contains DEFAULT_CURRENCY_CODE
        defaultBitcoinAnalyzeShouldBeFound("currencyCode.contains=" + DEFAULT_CURRENCY_CODE);

        // Get all the bitcoinAnalyzeList where currencyCode contains UPDATED_CURRENCY_CODE
        defaultBitcoinAnalyzeShouldNotBeFound("currencyCode.contains=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCurrencyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where currencyCode does not contain DEFAULT_CURRENCY_CODE
        defaultBitcoinAnalyzeShouldNotBeFound("currencyCode.doesNotContain=" + DEFAULT_CURRENCY_CODE);

        // Get all the bitcoinAnalyzeList where currencyCode does not contain UPDATED_CURRENCY_CODE
        defaultBitcoinAnalyzeShouldBeFound("currencyCode.doesNotContain=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesBySymbolIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where symbol equals to DEFAULT_SYMBOL
        defaultBitcoinAnalyzeShouldBeFound("symbol.equals=" + DEFAULT_SYMBOL);

        // Get all the bitcoinAnalyzeList where symbol equals to UPDATED_SYMBOL
        defaultBitcoinAnalyzeShouldNotBeFound("symbol.equals=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesBySymbolIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where symbol not equals to DEFAULT_SYMBOL
        defaultBitcoinAnalyzeShouldNotBeFound("symbol.notEquals=" + DEFAULT_SYMBOL);

        // Get all the bitcoinAnalyzeList where symbol not equals to UPDATED_SYMBOL
        defaultBitcoinAnalyzeShouldBeFound("symbol.notEquals=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesBySymbolIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where symbol in DEFAULT_SYMBOL or UPDATED_SYMBOL
        defaultBitcoinAnalyzeShouldBeFound("symbol.in=" + DEFAULT_SYMBOL + "," + UPDATED_SYMBOL);

        // Get all the bitcoinAnalyzeList where symbol equals to UPDATED_SYMBOL
        defaultBitcoinAnalyzeShouldNotBeFound("symbol.in=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesBySymbolIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where symbol is not null
        defaultBitcoinAnalyzeShouldBeFound("symbol.specified=true");

        // Get all the bitcoinAnalyzeList where symbol is null
        defaultBitcoinAnalyzeShouldNotBeFound("symbol.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesBySymbolContainsSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where symbol contains DEFAULT_SYMBOL
        defaultBitcoinAnalyzeShouldBeFound("symbol.contains=" + DEFAULT_SYMBOL);

        // Get all the bitcoinAnalyzeList where symbol contains UPDATED_SYMBOL
        defaultBitcoinAnalyzeShouldNotBeFound("symbol.contains=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesBySymbolNotContainsSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where symbol does not contain DEFAULT_SYMBOL
        defaultBitcoinAnalyzeShouldNotBeFound("symbol.doesNotContain=" + DEFAULT_SYMBOL);

        // Get all the bitcoinAnalyzeList where symbol does not contain UPDATED_SYMBOL
        defaultBitcoinAnalyzeShouldBeFound("symbol.doesNotContain=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByOpenRateFloatIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where openRateFloat equals to DEFAULT_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("openRateFloat.equals=" + DEFAULT_OPEN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where openRateFloat equals to UPDATED_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("openRateFloat.equals=" + UPDATED_OPEN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByOpenRateFloatIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where openRateFloat not equals to DEFAULT_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("openRateFloat.notEquals=" + DEFAULT_OPEN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where openRateFloat not equals to UPDATED_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("openRateFloat.notEquals=" + UPDATED_OPEN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByOpenRateFloatIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where openRateFloat in DEFAULT_OPEN_RATE_FLOAT or UPDATED_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("openRateFloat.in=" + DEFAULT_OPEN_RATE_FLOAT + "," + UPDATED_OPEN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where openRateFloat equals to UPDATED_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("openRateFloat.in=" + UPDATED_OPEN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByOpenRateFloatIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where openRateFloat is not null
        defaultBitcoinAnalyzeShouldBeFound("openRateFloat.specified=true");

        // Get all the bitcoinAnalyzeList where openRateFloat is null
        defaultBitcoinAnalyzeShouldNotBeFound("openRateFloat.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByOpenRateFloatIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where openRateFloat is greater than or equal to DEFAULT_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("openRateFloat.greaterThanOrEqual=" + DEFAULT_OPEN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where openRateFloat is greater than or equal to UPDATED_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("openRateFloat.greaterThanOrEqual=" + UPDATED_OPEN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByOpenRateFloatIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where openRateFloat is less than or equal to DEFAULT_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("openRateFloat.lessThanOrEqual=" + DEFAULT_OPEN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where openRateFloat is less than or equal to SMALLER_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("openRateFloat.lessThanOrEqual=" + SMALLER_OPEN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByOpenRateFloatIsLessThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where openRateFloat is less than DEFAULT_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("openRateFloat.lessThan=" + DEFAULT_OPEN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where openRateFloat is less than UPDATED_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("openRateFloat.lessThan=" + UPDATED_OPEN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByOpenRateFloatIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where openRateFloat is greater than DEFAULT_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("openRateFloat.greaterThan=" + DEFAULT_OPEN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where openRateFloat is greater than SMALLER_OPEN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("openRateFloat.greaterThan=" + SMALLER_OPEN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByClosedRateFloatIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where closedRateFloat equals to DEFAULT_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("closedRateFloat.equals=" + DEFAULT_CLOSED_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where closedRateFloat equals to UPDATED_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("closedRateFloat.equals=" + UPDATED_CLOSED_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByClosedRateFloatIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where closedRateFloat not equals to DEFAULT_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("closedRateFloat.notEquals=" + DEFAULT_CLOSED_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where closedRateFloat not equals to UPDATED_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("closedRateFloat.notEquals=" + UPDATED_CLOSED_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByClosedRateFloatIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where closedRateFloat in DEFAULT_CLOSED_RATE_FLOAT or UPDATED_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("closedRateFloat.in=" + DEFAULT_CLOSED_RATE_FLOAT + "," + UPDATED_CLOSED_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where closedRateFloat equals to UPDATED_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("closedRateFloat.in=" + UPDATED_CLOSED_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByClosedRateFloatIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where closedRateFloat is not null
        defaultBitcoinAnalyzeShouldBeFound("closedRateFloat.specified=true");

        // Get all the bitcoinAnalyzeList where closedRateFloat is null
        defaultBitcoinAnalyzeShouldNotBeFound("closedRateFloat.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByClosedRateFloatIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where closedRateFloat is greater than or equal to DEFAULT_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("closedRateFloat.greaterThanOrEqual=" + DEFAULT_CLOSED_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where closedRateFloat is greater than or equal to UPDATED_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("closedRateFloat.greaterThanOrEqual=" + UPDATED_CLOSED_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByClosedRateFloatIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where closedRateFloat is less than or equal to DEFAULT_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("closedRateFloat.lessThanOrEqual=" + DEFAULT_CLOSED_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where closedRateFloat is less than or equal to SMALLER_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("closedRateFloat.lessThanOrEqual=" + SMALLER_CLOSED_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByClosedRateFloatIsLessThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where closedRateFloat is less than DEFAULT_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("closedRateFloat.lessThan=" + DEFAULT_CLOSED_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where closedRateFloat is less than UPDATED_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("closedRateFloat.lessThan=" + UPDATED_CLOSED_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByClosedRateFloatIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where closedRateFloat is greater than DEFAULT_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("closedRateFloat.greaterThan=" + DEFAULT_CLOSED_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where closedRateFloat is greater than SMALLER_CLOSED_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("closedRateFloat.greaterThan=" + SMALLER_CLOSED_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioOpenClosedRateIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate equals to DEFAULT_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldBeFound("ratioOpenClosedRate.equals=" + DEFAULT_RATIO_OPEN_CLOSED_RATE);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate equals to UPDATED_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldNotBeFound("ratioOpenClosedRate.equals=" + UPDATED_RATIO_OPEN_CLOSED_RATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioOpenClosedRateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate not equals to DEFAULT_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldNotBeFound("ratioOpenClosedRate.notEquals=" + DEFAULT_RATIO_OPEN_CLOSED_RATE);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate not equals to UPDATED_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldBeFound("ratioOpenClosedRate.notEquals=" + UPDATED_RATIO_OPEN_CLOSED_RATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioOpenClosedRateIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate in DEFAULT_RATIO_OPEN_CLOSED_RATE or UPDATED_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldBeFound(
            "ratioOpenClosedRate.in=" + DEFAULT_RATIO_OPEN_CLOSED_RATE + "," + UPDATED_RATIO_OPEN_CLOSED_RATE
        );

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate equals to UPDATED_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldNotBeFound("ratioOpenClosedRate.in=" + UPDATED_RATIO_OPEN_CLOSED_RATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioOpenClosedRateIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate is not null
        defaultBitcoinAnalyzeShouldBeFound("ratioOpenClosedRate.specified=true");

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate is null
        defaultBitcoinAnalyzeShouldNotBeFound("ratioOpenClosedRate.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioOpenClosedRateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate is greater than or equal to DEFAULT_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldBeFound("ratioOpenClosedRate.greaterThanOrEqual=" + DEFAULT_RATIO_OPEN_CLOSED_RATE);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate is greater than or equal to UPDATED_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldNotBeFound("ratioOpenClosedRate.greaterThanOrEqual=" + UPDATED_RATIO_OPEN_CLOSED_RATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioOpenClosedRateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate is less than or equal to DEFAULT_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldBeFound("ratioOpenClosedRate.lessThanOrEqual=" + DEFAULT_RATIO_OPEN_CLOSED_RATE);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate is less than or equal to SMALLER_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldNotBeFound("ratioOpenClosedRate.lessThanOrEqual=" + SMALLER_RATIO_OPEN_CLOSED_RATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioOpenClosedRateIsLessThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate is less than DEFAULT_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldNotBeFound("ratioOpenClosedRate.lessThan=" + DEFAULT_RATIO_OPEN_CLOSED_RATE);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate is less than UPDATED_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldBeFound("ratioOpenClosedRate.lessThan=" + UPDATED_RATIO_OPEN_CLOSED_RATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioOpenClosedRateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate is greater than DEFAULT_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldNotBeFound("ratioOpenClosedRate.greaterThan=" + DEFAULT_RATIO_OPEN_CLOSED_RATE);

        // Get all the bitcoinAnalyzeList where ratioOpenClosedRate is greater than SMALLER_RATIO_OPEN_CLOSED_RATE
        defaultBitcoinAnalyzeShouldBeFound("ratioOpenClosedRate.greaterThan=" + SMALLER_RATIO_OPEN_CLOSED_RATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMaxRateFloatIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where maxRateFloat equals to DEFAULT_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("maxRateFloat.equals=" + DEFAULT_MAX_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where maxRateFloat equals to UPDATED_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("maxRateFloat.equals=" + UPDATED_MAX_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMaxRateFloatIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where maxRateFloat not equals to DEFAULT_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("maxRateFloat.notEquals=" + DEFAULT_MAX_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where maxRateFloat not equals to UPDATED_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("maxRateFloat.notEquals=" + UPDATED_MAX_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMaxRateFloatIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where maxRateFloat in DEFAULT_MAX_RATE_FLOAT or UPDATED_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("maxRateFloat.in=" + DEFAULT_MAX_RATE_FLOAT + "," + UPDATED_MAX_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where maxRateFloat equals to UPDATED_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("maxRateFloat.in=" + UPDATED_MAX_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMaxRateFloatIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where maxRateFloat is not null
        defaultBitcoinAnalyzeShouldBeFound("maxRateFloat.specified=true");

        // Get all the bitcoinAnalyzeList where maxRateFloat is null
        defaultBitcoinAnalyzeShouldNotBeFound("maxRateFloat.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMaxRateFloatIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where maxRateFloat is greater than or equal to DEFAULT_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("maxRateFloat.greaterThanOrEqual=" + DEFAULT_MAX_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where maxRateFloat is greater than or equal to UPDATED_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("maxRateFloat.greaterThanOrEqual=" + UPDATED_MAX_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMaxRateFloatIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where maxRateFloat is less than or equal to DEFAULT_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("maxRateFloat.lessThanOrEqual=" + DEFAULT_MAX_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where maxRateFloat is less than or equal to SMALLER_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("maxRateFloat.lessThanOrEqual=" + SMALLER_MAX_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMaxRateFloatIsLessThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where maxRateFloat is less than DEFAULT_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("maxRateFloat.lessThan=" + DEFAULT_MAX_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where maxRateFloat is less than UPDATED_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("maxRateFloat.lessThan=" + UPDATED_MAX_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMaxRateFloatIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where maxRateFloat is greater than DEFAULT_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("maxRateFloat.greaterThan=" + DEFAULT_MAX_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where maxRateFloat is greater than SMALLER_MAX_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("maxRateFloat.greaterThan=" + SMALLER_MAX_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMinRateFloatIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where minRateFloat equals to DEFAULT_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("minRateFloat.equals=" + DEFAULT_MIN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where minRateFloat equals to UPDATED_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("minRateFloat.equals=" + UPDATED_MIN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMinRateFloatIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where minRateFloat not equals to DEFAULT_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("minRateFloat.notEquals=" + DEFAULT_MIN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where minRateFloat not equals to UPDATED_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("minRateFloat.notEquals=" + UPDATED_MIN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMinRateFloatIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where minRateFloat in DEFAULT_MIN_RATE_FLOAT or UPDATED_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("minRateFloat.in=" + DEFAULT_MIN_RATE_FLOAT + "," + UPDATED_MIN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where minRateFloat equals to UPDATED_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("minRateFloat.in=" + UPDATED_MIN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMinRateFloatIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where minRateFloat is not null
        defaultBitcoinAnalyzeShouldBeFound("minRateFloat.specified=true");

        // Get all the bitcoinAnalyzeList where minRateFloat is null
        defaultBitcoinAnalyzeShouldNotBeFound("minRateFloat.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMinRateFloatIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where minRateFloat is greater than or equal to DEFAULT_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("minRateFloat.greaterThanOrEqual=" + DEFAULT_MIN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where minRateFloat is greater than or equal to UPDATED_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("minRateFloat.greaterThanOrEqual=" + UPDATED_MIN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMinRateFloatIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where minRateFloat is less than or equal to DEFAULT_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("minRateFloat.lessThanOrEqual=" + DEFAULT_MIN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where minRateFloat is less than or equal to SMALLER_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("minRateFloat.lessThanOrEqual=" + SMALLER_MIN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMinRateFloatIsLessThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where minRateFloat is less than DEFAULT_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("minRateFloat.lessThan=" + DEFAULT_MIN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where minRateFloat is less than UPDATED_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("minRateFloat.lessThan=" + UPDATED_MIN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByMinRateFloatIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where minRateFloat is greater than DEFAULT_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("minRateFloat.greaterThan=" + DEFAULT_MIN_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where minRateFloat is greater than SMALLER_MIN_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("minRateFloat.greaterThan=" + SMALLER_MIN_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioRateFloatIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioRateFloat equals to DEFAULT_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("ratioRateFloat.equals=" + DEFAULT_RATIO_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where ratioRateFloat equals to UPDATED_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("ratioRateFloat.equals=" + UPDATED_RATIO_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioRateFloatIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioRateFloat not equals to DEFAULT_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("ratioRateFloat.notEquals=" + DEFAULT_RATIO_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where ratioRateFloat not equals to UPDATED_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("ratioRateFloat.notEquals=" + UPDATED_RATIO_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioRateFloatIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioRateFloat in DEFAULT_RATIO_RATE_FLOAT or UPDATED_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("ratioRateFloat.in=" + DEFAULT_RATIO_RATE_FLOAT + "," + UPDATED_RATIO_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where ratioRateFloat equals to UPDATED_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("ratioRateFloat.in=" + UPDATED_RATIO_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioRateFloatIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioRateFloat is not null
        defaultBitcoinAnalyzeShouldBeFound("ratioRateFloat.specified=true");

        // Get all the bitcoinAnalyzeList where ratioRateFloat is null
        defaultBitcoinAnalyzeShouldNotBeFound("ratioRateFloat.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioRateFloatIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioRateFloat is greater than or equal to DEFAULT_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("ratioRateFloat.greaterThanOrEqual=" + DEFAULT_RATIO_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where ratioRateFloat is greater than or equal to UPDATED_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("ratioRateFloat.greaterThanOrEqual=" + UPDATED_RATIO_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioRateFloatIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioRateFloat is less than or equal to DEFAULT_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("ratioRateFloat.lessThanOrEqual=" + DEFAULT_RATIO_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where ratioRateFloat is less than or equal to SMALLER_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("ratioRateFloat.lessThanOrEqual=" + SMALLER_RATIO_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioRateFloatIsLessThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioRateFloat is less than DEFAULT_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("ratioRateFloat.lessThan=" + DEFAULT_RATIO_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where ratioRateFloat is less than UPDATED_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("ratioRateFloat.lessThan=" + UPDATED_RATIO_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByRatioRateFloatIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where ratioRateFloat is greater than DEFAULT_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldNotBeFound("ratioRateFloat.greaterThan=" + DEFAULT_RATIO_RATE_FLOAT);

        // Get all the bitcoinAnalyzeList where ratioRateFloat is greater than SMALLER_RATIO_RATE_FLOAT
        defaultBitcoinAnalyzeShouldBeFound("ratioRateFloat.greaterThan=" + SMALLER_RATIO_RATE_FLOAT);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where description equals to DEFAULT_DESCRIPTION
        defaultBitcoinAnalyzeShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the bitcoinAnalyzeList where description equals to UPDATED_DESCRIPTION
        defaultBitcoinAnalyzeShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where description not equals to DEFAULT_DESCRIPTION
        defaultBitcoinAnalyzeShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the bitcoinAnalyzeList where description not equals to UPDATED_DESCRIPTION
        defaultBitcoinAnalyzeShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultBitcoinAnalyzeShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the bitcoinAnalyzeList where description equals to UPDATED_DESCRIPTION
        defaultBitcoinAnalyzeShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where description is not null
        defaultBitcoinAnalyzeShouldBeFound("description.specified=true");

        // Get all the bitcoinAnalyzeList where description is null
        defaultBitcoinAnalyzeShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where description contains DEFAULT_DESCRIPTION
        defaultBitcoinAnalyzeShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the bitcoinAnalyzeList where description contains UPDATED_DESCRIPTION
        defaultBitcoinAnalyzeShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where description does not contain DEFAULT_DESCRIPTION
        defaultBitcoinAnalyzeShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the bitcoinAnalyzeList where description does not contain UPDATED_DESCRIPTION
        defaultBitcoinAnalyzeShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where createdDate equals to DEFAULT_CREATED_DATE
        defaultBitcoinAnalyzeShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the bitcoinAnalyzeList where createdDate equals to UPDATED_CREATED_DATE
        defaultBitcoinAnalyzeShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCreatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where createdDate not equals to DEFAULT_CREATED_DATE
        defaultBitcoinAnalyzeShouldNotBeFound("createdDate.notEquals=" + DEFAULT_CREATED_DATE);

        // Get all the bitcoinAnalyzeList where createdDate not equals to UPDATED_CREATED_DATE
        defaultBitcoinAnalyzeShouldBeFound("createdDate.notEquals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultBitcoinAnalyzeShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the bitcoinAnalyzeList where createdDate equals to UPDATED_CREATED_DATE
        defaultBitcoinAnalyzeShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where createdDate is not null
        defaultBitcoinAnalyzeShouldBeFound("createdDate.specified=true");

        // Get all the bitcoinAnalyzeList where createdDate is null
        defaultBitcoinAnalyzeShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where createdDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultBitcoinAnalyzeShouldBeFound("createdDate.greaterThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the bitcoinAnalyzeList where createdDate is greater than or equal to UPDATED_CREATED_DATE
        defaultBitcoinAnalyzeShouldNotBeFound("createdDate.greaterThanOrEqual=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where createdDate is less than or equal to DEFAULT_CREATED_DATE
        defaultBitcoinAnalyzeShouldBeFound("createdDate.lessThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the bitcoinAnalyzeList where createdDate is less than or equal to SMALLER_CREATED_DATE
        defaultBitcoinAnalyzeShouldNotBeFound("createdDate.lessThanOrEqual=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCreatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where createdDate is less than DEFAULT_CREATED_DATE
        defaultBitcoinAnalyzeShouldNotBeFound("createdDate.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the bitcoinAnalyzeList where createdDate is less than UPDATED_CREATED_DATE
        defaultBitcoinAnalyzeShouldBeFound("createdDate.lessThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCreatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where createdDate is greater than DEFAULT_CREATED_DATE
        defaultBitcoinAnalyzeShouldNotBeFound("createdDate.greaterThan=" + DEFAULT_CREATED_DATE);

        // Get all the bitcoinAnalyzeList where createdDate is greater than SMALLER_CREATED_DATE
        defaultBitcoinAnalyzeShouldBeFound("createdDate.greaterThan=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCreatedTimeStampIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where createdTimeStamp equals to DEFAULT_CREATED_TIME_STAMP
        defaultBitcoinAnalyzeShouldBeFound("createdTimeStamp.equals=" + DEFAULT_CREATED_TIME_STAMP);

        // Get all the bitcoinAnalyzeList where createdTimeStamp equals to UPDATED_CREATED_TIME_STAMP
        defaultBitcoinAnalyzeShouldNotBeFound("createdTimeStamp.equals=" + UPDATED_CREATED_TIME_STAMP);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCreatedTimeStampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where createdTimeStamp not equals to DEFAULT_CREATED_TIME_STAMP
        defaultBitcoinAnalyzeShouldNotBeFound("createdTimeStamp.notEquals=" + DEFAULT_CREATED_TIME_STAMP);

        // Get all the bitcoinAnalyzeList where createdTimeStamp not equals to UPDATED_CREATED_TIME_STAMP
        defaultBitcoinAnalyzeShouldBeFound("createdTimeStamp.notEquals=" + UPDATED_CREATED_TIME_STAMP);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCreatedTimeStampIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where createdTimeStamp in DEFAULT_CREATED_TIME_STAMP or UPDATED_CREATED_TIME_STAMP
        defaultBitcoinAnalyzeShouldBeFound("createdTimeStamp.in=" + DEFAULT_CREATED_TIME_STAMP + "," + UPDATED_CREATED_TIME_STAMP);

        // Get all the bitcoinAnalyzeList where createdTimeStamp equals to UPDATED_CREATED_TIME_STAMP
        defaultBitcoinAnalyzeShouldNotBeFound("createdTimeStamp.in=" + UPDATED_CREATED_TIME_STAMP);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByCreatedTimeStampIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where createdTimeStamp is not null
        defaultBitcoinAnalyzeShouldBeFound("createdTimeStamp.specified=true");

        // Get all the bitcoinAnalyzeList where createdTimeStamp is null
        defaultBitcoinAnalyzeShouldNotBeFound("createdTimeStamp.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByTimeTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where timeType equals to DEFAULT_TIME_TYPE
        defaultBitcoinAnalyzeShouldBeFound("timeType.equals=" + DEFAULT_TIME_TYPE);

        // Get all the bitcoinAnalyzeList where timeType equals to UPDATED_TIME_TYPE
        defaultBitcoinAnalyzeShouldNotBeFound("timeType.equals=" + UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByTimeTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where timeType not equals to DEFAULT_TIME_TYPE
        defaultBitcoinAnalyzeShouldNotBeFound("timeType.notEquals=" + DEFAULT_TIME_TYPE);

        // Get all the bitcoinAnalyzeList where timeType not equals to UPDATED_TIME_TYPE
        defaultBitcoinAnalyzeShouldBeFound("timeType.notEquals=" + UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByTimeTypeIsInShouldWork() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where timeType in DEFAULT_TIME_TYPE or UPDATED_TIME_TYPE
        defaultBitcoinAnalyzeShouldBeFound("timeType.in=" + DEFAULT_TIME_TYPE + "," + UPDATED_TIME_TYPE);

        // Get all the bitcoinAnalyzeList where timeType equals to UPDATED_TIME_TYPE
        defaultBitcoinAnalyzeShouldNotBeFound("timeType.in=" + UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByTimeTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where timeType is not null
        defaultBitcoinAnalyzeShouldBeFound("timeType.specified=true");

        // Get all the bitcoinAnalyzeList where timeType is null
        defaultBitcoinAnalyzeShouldNotBeFound("timeType.specified=false");
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByTimeTypeContainsSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where timeType contains DEFAULT_TIME_TYPE
        defaultBitcoinAnalyzeShouldBeFound("timeType.contains=" + DEFAULT_TIME_TYPE);

        // Get all the bitcoinAnalyzeList where timeType contains UPDATED_TIME_TYPE
        defaultBitcoinAnalyzeShouldNotBeFound("timeType.contains=" + UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void getAllBitcoinAnalyzesByTimeTypeNotContainsSomething() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        // Get all the bitcoinAnalyzeList where timeType does not contain DEFAULT_TIME_TYPE
        defaultBitcoinAnalyzeShouldNotBeFound("timeType.doesNotContain=" + DEFAULT_TIME_TYPE);

        // Get all the bitcoinAnalyzeList where timeType does not contain UPDATED_TIME_TYPE
        defaultBitcoinAnalyzeShouldBeFound("timeType.doesNotContain=" + UPDATED_TIME_TYPE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBitcoinAnalyzeShouldBeFound(String filter) throws Exception {
        restBitcoinAnalyzeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bitcoinAnalyze.getId().intValue())))
            .andExpect(jsonPath("$.[*].marketApiId").value(hasItem(DEFAULT_MARKET_API_ID)))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].openRateFloat").value(hasItem(sameNumber(DEFAULT_OPEN_RATE_FLOAT))))
            .andExpect(jsonPath("$.[*].closedRateFloat").value(hasItem(sameNumber(DEFAULT_CLOSED_RATE_FLOAT))))
            .andExpect(jsonPath("$.[*].ratioOpenClosedRate").value(hasItem(sameNumber(DEFAULT_RATIO_OPEN_CLOSED_RATE))))
            .andExpect(jsonPath("$.[*].maxRateFloat").value(hasItem(sameNumber(DEFAULT_MAX_RATE_FLOAT))))
            .andExpect(jsonPath("$.[*].minRateFloat").value(hasItem(sameNumber(DEFAULT_MIN_RATE_FLOAT))))
            .andExpect(jsonPath("$.[*].ratioRateFloat").value(hasItem(sameNumber(DEFAULT_RATIO_RATE_FLOAT))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdTimeStamp").value(hasItem(DEFAULT_CREATED_TIME_STAMP.toString())))
            .andExpect(jsonPath("$.[*].timeType").value(hasItem(DEFAULT_TIME_TYPE)));

        // Check, that the count call also returns 1
        restBitcoinAnalyzeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBitcoinAnalyzeShouldNotBeFound(String filter) throws Exception {
        restBitcoinAnalyzeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBitcoinAnalyzeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBitcoinAnalyze() throws Exception {
        // Get the bitcoinAnalyze
        restBitcoinAnalyzeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBitcoinAnalyze() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        int databaseSizeBeforeUpdate = bitcoinAnalyzeRepository.findAll().size();

        // Update the bitcoinAnalyze
        BitcoinAnalyze updatedBitcoinAnalyze = bitcoinAnalyzeRepository.findById(bitcoinAnalyze.getId()).get();
        // Disconnect from session so that the updates on updatedBitcoinAnalyze are not directly saved in db
        em.detach(updatedBitcoinAnalyze);
        updatedBitcoinAnalyze
            .marketApiId(UPDATED_MARKET_API_ID)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .symbol(UPDATED_SYMBOL)
            .openRateFloat(UPDATED_OPEN_RATE_FLOAT)
            .closedRateFloat(UPDATED_CLOSED_RATE_FLOAT)
            .ratioOpenClosedRate(UPDATED_RATIO_OPEN_CLOSED_RATE)
            .maxRateFloat(UPDATED_MAX_RATE_FLOAT)
            .minRateFloat(UPDATED_MIN_RATE_FLOAT)
            .ratioRateFloat(UPDATED_RATIO_RATE_FLOAT)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .createdTimeStamp(UPDATED_CREATED_TIME_STAMP)
            .timeType(UPDATED_TIME_TYPE);
        BitcoinAnalyzeDTO bitcoinAnalyzeDTO = bitcoinAnalyzeMapper.toDto(updatedBitcoinAnalyze);

        restBitcoinAnalyzeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bitcoinAnalyzeDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bitcoinAnalyzeDTO))
            )
            .andExpect(status().isOk());

        // Validate the BitcoinAnalyze in the database
        List<BitcoinAnalyze> bitcoinAnalyzeList = bitcoinAnalyzeRepository.findAll();
        assertThat(bitcoinAnalyzeList).hasSize(databaseSizeBeforeUpdate);
        BitcoinAnalyze testBitcoinAnalyze = bitcoinAnalyzeList.get(bitcoinAnalyzeList.size() - 1);
        assertThat(testBitcoinAnalyze.getMarketApiId()).isEqualTo(UPDATED_MARKET_API_ID);
        assertThat(testBitcoinAnalyze.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
        assertThat(testBitcoinAnalyze.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testBitcoinAnalyze.getOpenRateFloat()).isEqualByComparingTo(UPDATED_OPEN_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getClosedRateFloat()).isEqualByComparingTo(UPDATED_CLOSED_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getRatioOpenClosedRate()).isEqualByComparingTo(UPDATED_RATIO_OPEN_CLOSED_RATE);
        assertThat(testBitcoinAnalyze.getMaxRateFloat()).isEqualByComparingTo(UPDATED_MAX_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getMinRateFloat()).isEqualByComparingTo(UPDATED_MIN_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getRatioRateFloat()).isEqualByComparingTo(UPDATED_RATIO_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBitcoinAnalyze.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testBitcoinAnalyze.getCreatedTimeStamp()).isEqualTo(UPDATED_CREATED_TIME_STAMP);
        assertThat(testBitcoinAnalyze.getTimeType()).isEqualTo(UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingBitcoinAnalyze() throws Exception {
        int databaseSizeBeforeUpdate = bitcoinAnalyzeRepository.findAll().size();
        bitcoinAnalyze.setId(count.incrementAndGet());

        // Create the BitcoinAnalyze
        BitcoinAnalyzeDTO bitcoinAnalyzeDTO = bitcoinAnalyzeMapper.toDto(bitcoinAnalyze);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBitcoinAnalyzeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bitcoinAnalyzeDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bitcoinAnalyzeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BitcoinAnalyze in the database
        List<BitcoinAnalyze> bitcoinAnalyzeList = bitcoinAnalyzeRepository.findAll();
        assertThat(bitcoinAnalyzeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBitcoinAnalyze() throws Exception {
        int databaseSizeBeforeUpdate = bitcoinAnalyzeRepository.findAll().size();
        bitcoinAnalyze.setId(count.incrementAndGet());

        // Create the BitcoinAnalyze
        BitcoinAnalyzeDTO bitcoinAnalyzeDTO = bitcoinAnalyzeMapper.toDto(bitcoinAnalyze);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBitcoinAnalyzeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bitcoinAnalyzeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BitcoinAnalyze in the database
        List<BitcoinAnalyze> bitcoinAnalyzeList = bitcoinAnalyzeRepository.findAll();
        assertThat(bitcoinAnalyzeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBitcoinAnalyze() throws Exception {
        int databaseSizeBeforeUpdate = bitcoinAnalyzeRepository.findAll().size();
        bitcoinAnalyze.setId(count.incrementAndGet());

        // Create the BitcoinAnalyze
        BitcoinAnalyzeDTO bitcoinAnalyzeDTO = bitcoinAnalyzeMapper.toDto(bitcoinAnalyze);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBitcoinAnalyzeMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bitcoinAnalyzeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BitcoinAnalyze in the database
        List<BitcoinAnalyze> bitcoinAnalyzeList = bitcoinAnalyzeRepository.findAll();
        assertThat(bitcoinAnalyzeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBitcoinAnalyzeWithPatch() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        int databaseSizeBeforeUpdate = bitcoinAnalyzeRepository.findAll().size();

        // Update the bitcoinAnalyze using partial update
        BitcoinAnalyze partialUpdatedBitcoinAnalyze = new BitcoinAnalyze();
        partialUpdatedBitcoinAnalyze.setId(bitcoinAnalyze.getId());

        partialUpdatedBitcoinAnalyze
            .closedRateFloat(UPDATED_CLOSED_RATE_FLOAT)
            .ratioOpenClosedRate(UPDATED_RATIO_OPEN_CLOSED_RATE)
            .ratioRateFloat(UPDATED_RATIO_RATE_FLOAT)
            .createdDate(UPDATED_CREATED_DATE)
            .timeType(UPDATED_TIME_TYPE);

        restBitcoinAnalyzeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBitcoinAnalyze.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBitcoinAnalyze))
            )
            .andExpect(status().isOk());

        // Validate the BitcoinAnalyze in the database
        List<BitcoinAnalyze> bitcoinAnalyzeList = bitcoinAnalyzeRepository.findAll();
        assertThat(bitcoinAnalyzeList).hasSize(databaseSizeBeforeUpdate);
        BitcoinAnalyze testBitcoinAnalyze = bitcoinAnalyzeList.get(bitcoinAnalyzeList.size() - 1);
        assertThat(testBitcoinAnalyze.getMarketApiId()).isEqualTo(DEFAULT_MARKET_API_ID);
        assertThat(testBitcoinAnalyze.getCurrencyCode()).isEqualTo(DEFAULT_CURRENCY_CODE);
        assertThat(testBitcoinAnalyze.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testBitcoinAnalyze.getOpenRateFloat()).isEqualByComparingTo(DEFAULT_OPEN_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getClosedRateFloat()).isEqualByComparingTo(UPDATED_CLOSED_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getRatioOpenClosedRate()).isEqualByComparingTo(UPDATED_RATIO_OPEN_CLOSED_RATE);
        assertThat(testBitcoinAnalyze.getMaxRateFloat()).isEqualByComparingTo(DEFAULT_MAX_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getMinRateFloat()).isEqualByComparingTo(DEFAULT_MIN_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getRatioRateFloat()).isEqualByComparingTo(UPDATED_RATIO_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBitcoinAnalyze.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testBitcoinAnalyze.getCreatedTimeStamp()).isEqualTo(DEFAULT_CREATED_TIME_STAMP);
        assertThat(testBitcoinAnalyze.getTimeType()).isEqualTo(UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateBitcoinAnalyzeWithPatch() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        int databaseSizeBeforeUpdate = bitcoinAnalyzeRepository.findAll().size();

        // Update the bitcoinAnalyze using partial update
        BitcoinAnalyze partialUpdatedBitcoinAnalyze = new BitcoinAnalyze();
        partialUpdatedBitcoinAnalyze.setId(bitcoinAnalyze.getId());

        partialUpdatedBitcoinAnalyze
            .marketApiId(UPDATED_MARKET_API_ID)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .symbol(UPDATED_SYMBOL)
            .openRateFloat(UPDATED_OPEN_RATE_FLOAT)
            .closedRateFloat(UPDATED_CLOSED_RATE_FLOAT)
            .ratioOpenClosedRate(UPDATED_RATIO_OPEN_CLOSED_RATE)
            .maxRateFloat(UPDATED_MAX_RATE_FLOAT)
            .minRateFloat(UPDATED_MIN_RATE_FLOAT)
            .ratioRateFloat(UPDATED_RATIO_RATE_FLOAT)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .createdTimeStamp(UPDATED_CREATED_TIME_STAMP)
            .timeType(UPDATED_TIME_TYPE);

        restBitcoinAnalyzeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBitcoinAnalyze.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBitcoinAnalyze))
            )
            .andExpect(status().isOk());

        // Validate the BitcoinAnalyze in the database
        List<BitcoinAnalyze> bitcoinAnalyzeList = bitcoinAnalyzeRepository.findAll();
        assertThat(bitcoinAnalyzeList).hasSize(databaseSizeBeforeUpdate);
        BitcoinAnalyze testBitcoinAnalyze = bitcoinAnalyzeList.get(bitcoinAnalyzeList.size() - 1);
        assertThat(testBitcoinAnalyze.getMarketApiId()).isEqualTo(UPDATED_MARKET_API_ID);
        assertThat(testBitcoinAnalyze.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
        assertThat(testBitcoinAnalyze.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testBitcoinAnalyze.getOpenRateFloat()).isEqualByComparingTo(UPDATED_OPEN_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getClosedRateFloat()).isEqualByComparingTo(UPDATED_CLOSED_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getRatioOpenClosedRate()).isEqualByComparingTo(UPDATED_RATIO_OPEN_CLOSED_RATE);
        assertThat(testBitcoinAnalyze.getMaxRateFloat()).isEqualByComparingTo(UPDATED_MAX_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getMinRateFloat()).isEqualByComparingTo(UPDATED_MIN_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getRatioRateFloat()).isEqualByComparingTo(UPDATED_RATIO_RATE_FLOAT);
        assertThat(testBitcoinAnalyze.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBitcoinAnalyze.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testBitcoinAnalyze.getCreatedTimeStamp()).isEqualTo(UPDATED_CREATED_TIME_STAMP);
        assertThat(testBitcoinAnalyze.getTimeType()).isEqualTo(UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingBitcoinAnalyze() throws Exception {
        int databaseSizeBeforeUpdate = bitcoinAnalyzeRepository.findAll().size();
        bitcoinAnalyze.setId(count.incrementAndGet());

        // Create the BitcoinAnalyze
        BitcoinAnalyzeDTO bitcoinAnalyzeDTO = bitcoinAnalyzeMapper.toDto(bitcoinAnalyze);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBitcoinAnalyzeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bitcoinAnalyzeDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bitcoinAnalyzeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BitcoinAnalyze in the database
        List<BitcoinAnalyze> bitcoinAnalyzeList = bitcoinAnalyzeRepository.findAll();
        assertThat(bitcoinAnalyzeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBitcoinAnalyze() throws Exception {
        int databaseSizeBeforeUpdate = bitcoinAnalyzeRepository.findAll().size();
        bitcoinAnalyze.setId(count.incrementAndGet());

        // Create the BitcoinAnalyze
        BitcoinAnalyzeDTO bitcoinAnalyzeDTO = bitcoinAnalyzeMapper.toDto(bitcoinAnalyze);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBitcoinAnalyzeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bitcoinAnalyzeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BitcoinAnalyze in the database
        List<BitcoinAnalyze> bitcoinAnalyzeList = bitcoinAnalyzeRepository.findAll();
        assertThat(bitcoinAnalyzeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBitcoinAnalyze() throws Exception {
        int databaseSizeBeforeUpdate = bitcoinAnalyzeRepository.findAll().size();
        bitcoinAnalyze.setId(count.incrementAndGet());

        // Create the BitcoinAnalyze
        BitcoinAnalyzeDTO bitcoinAnalyzeDTO = bitcoinAnalyzeMapper.toDto(bitcoinAnalyze);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBitcoinAnalyzeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bitcoinAnalyzeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BitcoinAnalyze in the database
        List<BitcoinAnalyze> bitcoinAnalyzeList = bitcoinAnalyzeRepository.findAll();
        assertThat(bitcoinAnalyzeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBitcoinAnalyze() throws Exception {
        // Initialize the database
        bitcoinAnalyzeRepository.saveAndFlush(bitcoinAnalyze);

        int databaseSizeBeforeDelete = bitcoinAnalyzeRepository.findAll().size();

        // Delete the bitcoinAnalyze
        restBitcoinAnalyzeMockMvc
            .perform(delete(ENTITY_API_URL_ID, bitcoinAnalyze.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BitcoinAnalyze> bitcoinAnalyzeList = bitcoinAnalyzeRepository.findAll();
        assertThat(bitcoinAnalyzeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
