package myapp.bitcoin_analyze.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import myapp.bitcoin_analyze.domain.BitcoinExchange;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link myapp.bitcoin_analyze.domain.BitcoinExchange} entity.
 */
@Builder
public class BitcoinExchangeDTO implements Serializable {

    private Long id;

    private String currencyCode;

    private String symbol;

    private BigDecimal rateFloat;

    private LocalDate createdDate;

    private Instant createdTimeStamp;

    private MarketApiDTO marketApi;

    private Long cronJobId;


    public Long getCronJobId() {
        return this.cronJobId;
    }

    public void setCronJobId(Long cronJobId) {
        this.cronJobId = cronJobId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getRateFloat() {
        return rateFloat;
    }

    public void setRateFloat(BigDecimal rateFloat) {
        this.rateFloat = rateFloat;
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

    public MarketApiDTO getMarketApi() {
        return marketApi;
    }

    public void setMarketApi(MarketApiDTO marketApi) {
        this.marketApi = marketApi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BitcoinExchangeDTO)) {
            return false;
        }

        BitcoinExchangeDTO bitcoinExchangeDTO = (BitcoinExchangeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bitcoinExchangeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BitcoinExchangeDTO{" +
            "id=" + getId() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", symbol='" + getSymbol() + "'" +
            ", rateFloat=" + getRateFloat() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", createdTimeStamp='" + getCreatedTimeStamp() + "'" +
            ", marketApi=" + getMarketApi() +
            "}";
    }
}
