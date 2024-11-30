package myapp.bitcoin_analyze.service;

import java.util.Optional;
import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link myapp.bitcoin_analyze.domain.CronJob}.
 */
public interface CronJobService {
    /**
     * Save a cronJob.
     *
     * @param cronJobDTO the entity to save.
     * @return the persisted entity.
     */

    public CronJobDTO saveAndFlush(CronJobDTO cronJobDTO);
    CronJobDTO save(CronJobDTO cronJobDTO);

    /**
     * Updates a cronJob.
     *
     * @param cronJobDTO the entity to update.
     * @return the persisted entity.
     */
    CronJobDTO update(CronJobDTO cronJobDTO);

    /**
     * Partially updates a cronJob.
     *
     * @param cronJobDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CronJobDTO> partialUpdate(CronJobDTO cronJobDTO);

    /**
     * Get all the cronJobs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CronJobDTO> findAll(Pageable pageable);

    /**
     * Get the "id" cronJob.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CronJobDTO> findOne(Long id);

    /**
     * Delete the "id" cronJob.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
