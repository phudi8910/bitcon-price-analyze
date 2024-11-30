package myapp.bitcoin_analyze.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

/**
 * A MarketApi.
 */
@Entity
@Table(name = "market_api")
public class MarketApi implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "market_name")
    private String marketName;

    @Column(name = "url")
    private String url;

    @Column(name = "is_active")
    private String isActive;

    @Column(name = "description")
    private String description;

    @Column(name = "java_class")
    private String javaClass;

    @Column(name = "java_method")
    private String javaMethod;

    @Column(name = "ord")
    private Integer ord;

    @Transient
    private boolean isPersisted;

    @OneToMany(mappedBy = "marketApi")
    @JsonIgnoreProperties(value = { "marketApi" }, allowSetters = true)
    private Set<BitcoinExchange> bitcoinExchanges = new HashSet<>();

    public String getId() {
        return this.id;
    }

    public MarketApi id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarketName() {
        return this.marketName;
    }

    public MarketApi marketName(String marketName) {
        this.setMarketName(marketName);
        return this;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getUrl() {
        return this.url;
    }

    public MarketApi url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIsActive() {
        return this.isActive;
    }

    public MarketApi isActive(String isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getDescription() {
        return this.description;
    }

    public MarketApi description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrd() {
        return this.ord;
    }

    public MarketApi ord(Integer ord) {
        this.setOrd(ord);
        return this;
    }

    public String getJavaClass() {
        return this.javaClass;
    }

    public MarketApi javaClass(String javaClass) {
        this.setJavaClass(javaClass);
        return this;
    }

    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }

    public String getJavaMethod() {
        return this.javaMethod;
    }

    public MarketApi javaMethod(String javaMethod) {
        this.setJavaMethod(javaMethod);
        return this;
    }

    public void setJavaMethod(String javaMethod) {
        this.javaMethod = javaMethod;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public MarketApi setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    public Set<BitcoinExchange> getBitcoinExchanges() {
        return this.bitcoinExchanges;
    }

    public void setBitcoinExchanges(Set<BitcoinExchange> bitcoinExchanges) {
        if (this.bitcoinExchanges != null) {
            this.bitcoinExchanges.forEach(i -> i.setMarketApi(null));
        }
        if (bitcoinExchanges != null) {
            bitcoinExchanges.forEach(i -> i.setMarketApi(this));
        }
        this.bitcoinExchanges = bitcoinExchanges;
    }

    public MarketApi bitcoinExchanges(Set<BitcoinExchange> bitcoinExchanges) {
        this.setBitcoinExchanges(bitcoinExchanges);
        return this;
    }

    public MarketApi addBitcoinExchange(BitcoinExchange bitcoinExchange) {
        this.bitcoinExchanges.add(bitcoinExchange);
        bitcoinExchange.setMarketApi(this);
        return this;
    }

    public MarketApi removeBitcoinExchange(BitcoinExchange bitcoinExchange) {
        this.bitcoinExchanges.remove(bitcoinExchange);
        bitcoinExchange.setMarketApi(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MarketApi)) {
            return false;
        }
        return id != null && id.equals(((MarketApi) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MarketApi{" +
            "id=" + getId() +
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
