package myapp.bitcoin_analyze.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link myapp.bitcoin_analyze.domain.BitcoinAnalyze} entity.
 */
public class BitcoinAnalyzeDTO implements Serializable {

    private Long id;

    private String marketApiId;

    private String currencyCode;

    private String symbol;

    private BigDecimal openRateFloat;

    private BigDecimal closedRateFloat;

    private BigDecimal ratioOpenClosedRate;

    private BigDecimal maxRateFloat;

    private BigDecimal minRateFloat;

    private BigDecimal ratioRateFloat;

    private String description;

    private LocalDate createdDate;

    private Instant createdTimeStamp;

    private String timeType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarketApiId() {
        return marketApiId;
    }

    public void setMarketApiId(String marketApiId) {
        this.marketApiId = marketApiId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getOpenRateFloat() {
        return openRateFloat;
    }

    public void setOpenRateFloat(BigDecimal openRateFloat) {
        this.openRateFloat = openRateFloat;
    }

    public BigDecimal getClosedRateFloat() {
        return closedRateFloat;
    }

    public void setClosedRateFloat(BigDecimal closedRateFloat) {
        this.closedRateFloat = closedRateFloat;
    }

    public BigDecimal getRatioOpenClosedRate() {
        return ratioOpenClosedRate;
    }

    public void setRatioOpenClosedRate(BigDecimal ratioOpenClosedRate) {
        this.ratioOpenClosedRate = ratioOpenClosedRate;
    }

    public BigDecimal getMaxRateFloat() {
        return maxRateFloat;
    }

    public void setMaxRateFloat(BigDecimal maxRateFloat) {
        this.maxRateFloat = maxRateFloat;
    }

    public BigDecimal getMinRateFloat() {
        return minRateFloat;
    }

    public void setMinRateFloat(BigDecimal minRateFloat) {
        this.minRateFloat = minRateFloat;
    }

    public BigDecimal getRatioRateFloat() {
        return ratioRateFloat;
    }

    public void setRatioRateFloat(BigDecimal ratioRateFloat) {
        this.ratioRateFloat = ratioRateFloat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Instant createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BitcoinAnalyzeDTO)) {
            return false;
        }

        BitcoinAnalyzeDTO bitcoinAnalyzeDTO = (BitcoinAnalyzeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bitcoinAnalyzeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BitcoinAnalyzeDTO{" +
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
