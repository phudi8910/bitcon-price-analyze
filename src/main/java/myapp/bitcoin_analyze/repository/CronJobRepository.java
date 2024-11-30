package myapp.bitcoin_analyze.repository;

import myapp.bitcoin_analyze.domain.CronJob;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CronJob entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CronJobRepository extends JpaRepository<CronJob, Long>, JpaSpecificationExecutor<CronJob> {}
