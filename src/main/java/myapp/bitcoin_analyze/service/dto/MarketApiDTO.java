package myapp.bitcoin_analyze.service.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import myapp.bitcoin_analyze.domain.MarketApi;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A DTO for the {@link myapp.bitcoin_analyze.domain.MarketApi} entity.
 */
@Builder
public class MarketApiDTO implements Serializable {

    private String id;

    private String marketName;

    private String url;

    private String isActive;

    private String description;

    private String javaClass;

    private String javaMethod;

    private Integer ord;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJavaClass() {
        return this.javaClass;
    }

    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }

    public String getJavaMethod() {
        return this.javaMethod;
    }

    public void setJavaMethod(String javaMethod) {
        this.javaMethod = javaMethod;
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
        if (!(o instanceof MarketApiDTO)) {
            return false;
        }

        MarketApiDTO marketApiDTO = (MarketApiDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, marketApiDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MarketApiDTO{" +
            "id='" + getId() + "'" +
            ", marketName='" + getMarketName() + "'" +
            ", url='" + getUrl() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", description='" + getDescription() + "'" +
            ", javaClass='" + getJavaClass() + "'" +
            ", javaMethod='" + getJavaMethod() + "'" +
            ", ord=" + getOrd() +
            "}";
    }
}
