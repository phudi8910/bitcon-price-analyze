package myapp.bitcoin_analyze.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import myapp.bitcoin_analyze.service.dto.BitcoinAnalyzeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link myapp.bitcoin_analyze.domain.BitcoinAnalyze}.
 */
public interface BitcoinAnalyzeService {
    /**
     * Save a bitcoinAnalyze.
     *
     * @param bitcoinAnalyzeDTO the entity to save.
     * @return the persisted entity.
     */
    public List<BitcoinAnalyzeDTO> queryAnalyzeByTimeType(String timeType, String currencyCode,LocalDate fromDate, LocalDate toDate) throws Exception;

    public List<BitcoinAnalyzeDTO> initBitcoinAnalyze();

    public List<BitcoinAnalyzeDTO> processAnalyzeByTimeType(String timeType, String currencyCode)throws Exception;
    public List<BitcoinAnalyzeDTO> saveAll(List<BitcoinAnalyzeDTO> bitcoinAnalyzeDTOs);
    BitcoinAnalyzeDTO save(BitcoinAnalyzeDTO bitcoinAnalyzeDTO);

    /**
     * Updates a bitcoinAnalyze.
     *
     * @param bitcoinAnalyzeDTO the entity to update.
     * @return the persisted entity.
     */
    BitcoinAnalyzeDTO update(BitcoinAnalyzeDTO bitcoinAnalyzeDTO);

    /**
     * Partially updates a bitcoinAnalyze.
     *
     * @param bitcoinAnalyzeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BitcoinAnalyzeDTO> partialUpdate(BitcoinAnalyzeDTO bitcoinAnalyzeDTO);

    /**
     * Get all the bitcoinAnalyzes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BitcoinAnalyzeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" bitcoinAnalyze.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BitcoinAnalyzeDTO> findOne(Long id);

    /**
     * Delete the "id" bitcoinAnalyze.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    public List<BitcoinAnalyzeDTO> preparationAnalyzeByTimeType(LocalDate fromDate, LocalDate toDate, String timeType,String currencyCode)throws Exception;

}
