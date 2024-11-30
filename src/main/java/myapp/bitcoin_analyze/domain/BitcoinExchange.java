package myapp.bitcoin_analyze.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.ToString;

/**
 * A BitcoinExchange.
 */
@Entity
@Table(name = "bitcoin_exchange")
public class BitcoinExchange implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "rate_float", precision = 21, scale = 4)
    private BigDecimal rateFloat;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "created_time_stamp")
    private Instant createdTimeStamp;

    @Column(name = "cron_job_id")
    private Long cronJobId;

    @ManyToOne
    @JsonIgnoreProperties(value = { "bitcoinExchanges" }, allowSetters = true)
    private MarketApi marketApi;

    public Long getCronJobId() {
        return this.cronJobId;
    }

    public BitcoinExchange cronJobId(Long cronJobId) {
        this.setCronJobId(cronJobId);
        return this;
    }

    public void setCronJobId(Long cronJobId) {
        this.cronJobId = cronJobId;
    }

    public Long getId() {
        return this.id;
    }

    public BitcoinExchange id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public BitcoinExchange currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public BitcoinExchange symbol(String symbol) {
        this.setSymbol(symbol);
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getRateFloat() {
        return this.rateFloat;
    }

    public BitcoinExchange rateFloat(BigDecimal rateFloat) {
        this.setRateFloat(rateFloat);
        return this;
    }

    public void setRateFloat(BigDecimal rateFloat) {
        this.rateFloat = rateFloat;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    public BitcoinExchange createdDate(LocalDate createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getCreatedTimeStamp() {
        return this.createdTimeStamp;
    }

    public BitcoinExchange createdTimeStamp(Instant createdTimeStamp) {
        this.setCreatedTimeStamp(createdTimeStamp);
        return this;
    }

    public void setCreatedTimeStamp(Instant createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public MarketApi getMarketApi() {
        return this.marketApi;
    }

    public void setMarketApi(MarketApi marketApi) {
        this.marketApi = marketApi;
    }

    public BitcoinExchange marketApi(MarketApi marketApi) {
        this.setMarketApi(marketApi);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BitcoinExchange)) {
            return false;
        }
        return id != null && id.equals(((BitcoinExchange) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BitcoinExchange{" +
            "id=" + getId() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", symbol='" + getSymbol() + "'" +
            ", rateFloat=" + getRateFloat() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", createdTimeStamp='" + getCreatedTimeStamp() + "'" +
            "}";
    }
}
