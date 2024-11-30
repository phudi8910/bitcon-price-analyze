package myapp.bitcoin_analyze.service;

import java.util.List;
import java.util.Optional;
import myapp.bitcoin_analyze.service.dto.MarketApiDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link myapp.bitcoin_analyze.domain.MarketApi}.
 */
public interface MarketApiService {
    /**
     * Save a marketApi.
     *
     * @param marketApiDTO the entity to save.
     * @return the persisted entity.
     */

    public List<MarketApiDTO> findAllByIsActive(String isActive);

    MarketApiDTO save(MarketApiDTO marketApiDTO);

    /**
     * Updates a marketApi.
     *
     * @param marketApiDTO the entity to update.
     * @return the persisted entity.
     */
    MarketApiDTO update(MarketApiDTO marketApiDTO);

    /**
     * Partially updates a marketApi.
     *
     * @param marketApiDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MarketApiDTO> partialUpdate(MarketApiDTO marketApiDTO);

    /**
     * Get all the marketApis.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MarketApiDTO> findAll(Pageable pageable);

    /**
     * Get the "id" marketApi.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MarketApiDTO> findOne(String id);

    /**
     * Delete the "id" marketApi.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
