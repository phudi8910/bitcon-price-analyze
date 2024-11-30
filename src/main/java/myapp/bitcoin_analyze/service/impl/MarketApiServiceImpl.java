package myapp.bitcoin_analyze.service.impl;

import java.util.List;
import java.util.Optional;
import myapp.bitcoin_analyze.domain.MarketApi;
import myapp.bitcoin_analyze.repository.MarketApiRepository;
import myapp.bitcoin_analyze.service.MarketApiService;
import myapp.bitcoin_analyze.service.dto.CurrencyListDTO;
import myapp.bitcoin_analyze.service.dto.MarketApiDTO;
import myapp.bitcoin_analyze.service.mapper.MarketApiMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MarketApi}.
 */
@Service
@Transactional
public class MarketApiServiceImpl implements MarketApiService {

    private final Logger log = LoggerFactory.getLogger(MarketApiServiceImpl.class);

    private final MarketApiRepository marketApiRepository;

    private final MarketApiMapper marketApiMapper;

    public MarketApiServiceImpl(MarketApiRepository marketApiRepository, MarketApiMapper marketApiMapper) {
        this.marketApiRepository = marketApiRepository;
        this.marketApiMapper = marketApiMapper;
    }

    @Override
    public MarketApiDTO save(MarketApiDTO marketApiDTO) {
        log.debug("Request to save MarketApi : {}", marketApiDTO);
        MarketApi marketApi = marketApiMapper.toEntity(marketApiDTO);
        marketApi = marketApiRepository.save(marketApi);
        return marketApiMapper.toDto(marketApi);
    }

    @Override
    public MarketApiDTO update(MarketApiDTO marketApiDTO) {
        log.debug("Request to save MarketApi : {}", marketApiDTO);
        MarketApi marketApi = marketApiMapper.toEntity(marketApiDTO);
        marketApi.setIsPersisted();
        marketApi = marketApiRepository.save(marketApi);
        return marketApiMapper.toDto(marketApi);
    }

    @Override
    public Optional<MarketApiDTO> partialUpdate(MarketApiDTO marketApiDTO) {
        log.debug("Request to partially update MarketApi : {}", marketApiDTO);

        return marketApiRepository
            .findById(marketApiDTO.getId())
            .map(existingMarketApi -> {
                marketApiMapper.partialUpdate(existingMarketApi, marketApiDTO);

                return existingMarketApi;
            })
            .map(marketApiRepository::save)
            .map(marketApiMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MarketApiDTO> findAll(Pageable pageable) {
        log.debug("Request to get all MarketApis");
        return marketApiRepository.findAll(pageable).map(marketApiMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MarketApiDTO> findOne(String id) {
        log.debug("Request to get MarketApi : {}", id);
        return marketApiRepository.findById(id).map(marketApiMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete MarketApi : {}", id);
        marketApiRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarketApiDTO> findAllByIsActive(String isActive) {
        log.debug("Request to get all MarketApi");
        return marketApiMapper.toDto(marketApiRepository.findAllByIsActive(isActive));
    }
}
