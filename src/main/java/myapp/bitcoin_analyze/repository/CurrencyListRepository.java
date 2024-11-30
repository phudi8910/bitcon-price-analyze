package myapp.bitcoin_analyze.repository;

import myapp.bitcoin_analyze.domain.CurrencyList;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the CurrencyList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CurrencyListRepository extends JpaRepository<CurrencyList, String>, JpaSpecificationExecutor<CurrencyList> {
    List<CurrencyList> findAllByIsActiveAndMarketApiId(String isActive,String marketApiId);

    boolean existsByMarketApiId(String marketId);

}
