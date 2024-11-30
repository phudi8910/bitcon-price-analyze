package myapp.bitcoin_analyze.domain;

import java.io.Serializable;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

/**
 * A CurrencyList.
 */
@Entity
@Table(name = "currency_list")
public class CurrencyList implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "market_api_id")
    private String marketApiId;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private String isActive;

    @Column(name = "ord")
    private Integer ord;

    @Transient
    private boolean isPersisted;

    public String getMarketApiId() {
        return this.marketApiId;
    }

    public CurrencyList marketApiId(String marketApiId) {
        this.setMarketApiId(marketApiId);
        return this;
    }

    public void setMarketApiId(String marketApiId) {
        this.marketApiId = marketApiId;
    }

    public String getId() {
        return this.id;
    }

    public CurrencyList id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public CurrencyList description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsActive() {
        return this.isActive;
    }

    public CurrencyList isActive(String isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public Integer getOrd() {
        return this.ord;
    }

    public CurrencyList ord(Integer ord) {
        this.setOrd(ord);
        return this;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public CurrencyList setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CurrencyList)) {
            return false;
        }
        return id != null && id.equals(((CurrencyList) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CurrencyList{" +
            "id=" + getId() +
                ", marketApiId='" + getMarketApiId() + "'" +
            ", description='" + getDescription() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", ord=" + getOrd() +
            "}";
    }
}
