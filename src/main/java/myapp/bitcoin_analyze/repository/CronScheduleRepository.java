package myapp.bitcoin_analyze.repository;

import myapp.bitcoin_analyze.domain.CronSchedule;
import myapp.bitcoin_analyze.domain.CurrencyList;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the CronSchedule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CronScheduleRepository extends JpaRepository<CronSchedule, Long>, JpaSpecificationExecutor<CronSchedule> {
    List<CronSchedule> findAllByIsActiveOrderByIdAsc(String isActive);
}
