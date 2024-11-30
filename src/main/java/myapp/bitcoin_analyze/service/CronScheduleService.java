package myapp.bitcoin_analyze.service;

import java.util.List;
import java.util.Optional;
import myapp.bitcoin_analyze.service.dto.CronScheduleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link myapp.bitcoin_analyze.domain.CronSchedule}.
 */
public interface CronScheduleService {
    /**
     * Save a cronSchedule.
     *
     * @param cronScheduleDTO the entity to save.
     * @return the persisted entity.
     */
    public List<CronScheduleDTO> findAllByIsActiveOrderByIdAsc(String isActive);

    CronScheduleDTO save(CronScheduleDTO cronScheduleDTO);

    /**
     * Updates a cronSchedule.
     *
     * @param cronScheduleDTO the entity to update.
     * @return the persisted entity.
     */
    CronScheduleDTO update(CronScheduleDTO cronScheduleDTO);

    /**
     * Partially updates a cronSchedule.
     *
     * @param cronScheduleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CronScheduleDTO> partialUpdate(CronScheduleDTO cronScheduleDTO);

    /**
     * Get all the cronSchedules.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CronScheduleDTO> findAll(Pageable pageable);

    /**
     * Get the "id" cronSchedule.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CronScheduleDTO> findOne(Long id);

    /**
     * Delete the "id" cronSchedule.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
