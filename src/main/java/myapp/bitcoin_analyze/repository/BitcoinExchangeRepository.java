package myapp.bitcoin_analyze.repository;

import myapp.bitcoin_analyze.domain.BitcoinExchange;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Spring Data SQL repository for the BitcoinExchange entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BitcoinExchangeRepository extends JpaRepository<BitcoinExchange, Long>, JpaSpecificationExecutor<BitcoinExchange> {
    @Transactional
    @Query(value = "select min(created_date) from bitcoin_exchange", nativeQuery = true)
    LocalDate getMinCreatedDate();
}
