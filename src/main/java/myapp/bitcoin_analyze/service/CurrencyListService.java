package myapp.bitcoin_analyze.service;

import java.util.List;
import java.util.Optional;
import myapp.bitcoin_analyze.service.dto.CurrencyListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link myapp.bitcoin_analyze.domain.CurrencyList}.
 */
public interface CurrencyListService {
    /**
     * Save a currencyList.
     *
     * @param //currencyListDTO the entity to save.
     * @return the persisted entity.
     */
    public List<CurrencyListDTO> findAllByIsActiveAndMarketApiId(String isActive,String marketApiId);

    CurrencyListDTO save(CurrencyListDTO currencyListDTO);

    /**
     * Updates a currencyList.
     *
     * @param currencyListDTO the entity to update.
     * @return the persisted entity.
     */
    CurrencyListDTO update(CurrencyListDTO currencyListDTO);

    /**
     * Partially updates a currencyList.
     *
     * @param currencyListDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CurrencyListDTO> partialUpdate(CurrencyListDTO currencyListDTO);

    /**
     * Get all the currencyLists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CurrencyListDTO> findAll(Pageable pageable);

    /**
     * Get the "id" currencyList.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CurrencyListDTO> findOne(String id);

    /**
     * Delete the "id" currencyList.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
