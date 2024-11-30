package myapp.bitcoin_analyze.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import jakarta.persistence.*;

/**
 * A BitcoinAnalyze.
 */
@Entity
@Table(name = "bitcoin_analyze")
public class BitcoinAnalyze implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "market_api_id")
    private String marketApiId;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "open_rate_float", precision = 21, scale = 2)
    private BigDecimal openRateFloat;

    @Column(name = "closed_rate_float", precision = 21, scale = 2)
    private BigDecimal closedRateFloat;

    @Column(name = "ratio_open_closed_rate", precision = 21, scale = 2)
    private BigDecimal ratioOpenClosedRate;

    @Column(name = "max_rate_float", precision = 21, scale = 2)
    private BigDecimal maxRateFloat;

    @Column(name = "min_rate_float", precision = 21, scale = 2)
    private BigDecimal minRateFloat;

    @Column(name = "ratio_rate_float", precision = 21, scale = 2)
    private BigDecimal ratioRateFloat;

    @Column(name = "description")
    private String description;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "created_time_stamp")
    private Instant createdTimeStamp;

    @Column(name = "time_type")
    private String timeType;

    public Long getId() {
        return this.id;
    }

    public BitcoinAnalyze id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarketApiId() {
        return this.marketApiId;
    }

    public BitcoinAnalyze marketApiId(String marketApiId) {
        this.setMarketApiId(marketApiId);
        return this;
    }

    public void setMarketApiId(String marketApiId) {
        this.marketApiId = marketApiId;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public BitcoinAnalyze currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public BitcoinAnalyze symbol(String symbol) {
        this.setSymbol(symbol);
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getOpenRateFloat() {
        return this.openRateFloat;
    }

    public BitcoinAnalyze openRateFloat(BigDecimal openRateFloat) {
        this.setOpenRateFloat(openRateFloat);
        return this;
    }

    public void setOpenRateFloat(BigDecimal openRateFloat) {
        this.openRateFloat = openRateFloat;
    }

    public BigDecimal getClosedRateFloat() {
        return this.closedRateFloat;
    }

    public BitcoinAnalyze closedRateFloat(BigDecimal closedRateFloat) {
        this.setClosedRateFloat(closedRateFloat);
        return this;
    }

    public void setClosedRateFloat(BigDecimal closedRateFloat) {
        this.closedRateFloat = closedRateFloat;
    }

    public BigDecimal getRatioOpenClosedRate() {
        return this.ratioOpenClosedRate;
    }

    public BitcoinAnalyze ratioOpenClosedRate(BigDecimal ratioOpenClosedRate) {
        this.setRatioOpenClosedRate(ratioOpenClosedRate);
        return this;
    }

    public void setRatioOpenClosedRate(BigDecimal ratioOpenClosedRate) {
        this.ratioOpenClosedRate = ratioOpenClosedRate;
    }

    public BigDecimal getMaxRateFloat() {
        return this.maxRateFloat;
    }

    public BitcoinAnalyze maxRateFloat(BigDecimal maxRateFloat) {
        this.setMaxRateFloat(maxRateFloat);
        return this;
    }

    public void setMaxRateFloat(BigDecimal maxRateFloat) {
        this.maxRateFloat = maxRateFloat;
    }

    public BigDecimal getMinRateFloat() {
        return this.minRateFloat;
    }

    public BitcoinAnalyze minRateFloat(BigDecimal minRateFloat) {
        this.setMinRateFloat(minRateFloat);
        return this;
    }

    public void setMinRateFloat(BigDecimal minRateFloat) {
        this.minRateFloat = minRateFloat;
    }

    public BigDecimal getRatioRateFloat() {
        return this.ratioRateFloat;
    }

    public BitcoinAnalyze ratioRateFloat(BigDecimal ratioRateFloat) {
        this.setRatioRateFloat(ratioRateFloat);
        return this;
    }

    public void setRatioRateFloat(BigDecimal ratioRateFloat) {
        this.ratioRateFloat = ratioRateFloat;
    }

    public String getDescription() {
        return this.description;
    }

    public BitcoinAnalyze description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    public BitcoinAnalyze createdDate(LocalDate createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getCreatedTimeStamp() {
        return this.createdTimeStamp;
    }

    public BitcoinAnalyze createdTimeStamp(Instant createdTimeStamp) {
        this.setCreatedTimeStamp(createdTimeStamp);
        return this;
    }

    public void setCreatedTimeStamp(Instant createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public String getTimeType() {
        return this.timeType;
    }

    public BitcoinAnalyze timeType(String timeType) {
        this.setTimeType(timeType);
        return this;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BitcoinAnalyze)) {
            return false;
        }
        return id != null && id.equals(((BitcoinAnalyze) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BitcoinAnalyze{" +
            "id=" + getId() +
            ", marketApiId='" + getMarketApiId() + "'" +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", symbol='" + getSymbol() + "'" +
            ", openRateFloat=" + getOpenRateFloat() +
            ", closedRateFloat=" + getClosedRateFloat() +
            ", ratioOpenClosedRate=" + getRatioOpenClosedRate() +
            ", maxRateFloat=" + getMaxRateFloat() +
            ", minRateFloat=" + getMinRateFloat() +
            ", ratioRateFloat=" + getRatioRateFloat() +
            ", description='" + getDescription() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", createdTimeStamp='" + getCreatedTimeStamp() + "'" +
            ", timeType='" + getTimeType() + "'" +
            "}";
    }
}
