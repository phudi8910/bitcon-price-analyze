package myapp.bitcoin_analyze.repository;

import myapp.bitcoin_analyze.domain.CurrencyList;
import myapp.bitcoin_analyze.domain.MarketApi;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the MarketApi entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MarketApiRepository extends JpaRepository<MarketApi, String>, JpaSpecificationExecutor<MarketApi> {
    List<MarketApi> findAllByIsActive(String isActive);
}
