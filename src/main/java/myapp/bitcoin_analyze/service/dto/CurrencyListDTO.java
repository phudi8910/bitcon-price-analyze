package myapp.bitcoin_analyze.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link myapp.bitcoin_analyze.domain.CurrencyList} entity.
 */
public class CurrencyListDTO implements Serializable {

    private String id;

    private String description;

    private String isActive;

    private Integer ord;

    private String marketApiId;

    public String getMarketApiId() {
        return marketApiId;
    }

    public void setMarketApiId(String marketApiId) {
        this.marketApiId = marketApiId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public Integer getOrd() {
        return ord;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CurrencyListDTO)) {
            return false;
        }

        CurrencyListDTO currencyListDTO = (CurrencyListDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, currencyListDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CurrencyListDTO{" +
            "id='" + getId() + "'" +
                ", marketApiId='" + getMarketApiId() + "'" +
            ", description='" + getDescription() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", ord=" + getOrd() +
            "}";
    }
}
