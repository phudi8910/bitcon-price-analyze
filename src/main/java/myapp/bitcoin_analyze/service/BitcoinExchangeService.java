package myapp.bitcoin_analyze.service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import myapp.bitcoin_analyze.service.dto.BitcoinExchangeDTO;
import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link myapp.bitcoin_analyze.domain.BitcoinExchange}.
 */
public interface BitcoinExchangeService {
    /**
     * Save a bitcoinExchange.
     *
     * @param bitcoinExchangeDTO the entity to save.
     * @return the persisted entity.
     */

    public LocalDate getMinCreatedDate();

    public List<BitcoinExchangeDTO> fetchCurrentPrice(CronJobDTO cronJobDTO) throws Exception;

    BitcoinExchangeDTO save(BitcoinExchangeDTO bitcoinExchangeDTO);

    /**
     * Updates a bitcoinExchange.
     *
     * @param bitcoinExchangeDTO the entity to update.
     * @return the persisted entity.
     */
    BitcoinExchangeDTO update(BitcoinExchangeDTO bitcoinExchangeDTO);

    /**
     * Partially updates a bitcoinExchange.
     *
     * @param bitcoinExchangeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BitcoinExchangeDTO> partialUpdate(BitcoinExchangeDTO bitcoinExchangeDTO);

    /**
     * Get all the bitcoinExchanges.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BitcoinExchangeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" bitcoinExchange.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BitcoinExchangeDTO> findOne(Long id);

    /**
     * Delete the "id" bitcoinExchange.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    public List<BitcoinExchangeDTO> saveAll(List<BitcoinExchangeDTO> bitcoinExchangeDTOs);
}
