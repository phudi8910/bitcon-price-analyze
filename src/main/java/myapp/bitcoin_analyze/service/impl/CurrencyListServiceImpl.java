package myapp.bitcoin_analyze.service.impl;

import java.util.List;
import java.util.Optional;
import myapp.bitcoin_analyze.domain.CurrencyList;
import myapp.bitcoin_analyze.repository.CurrencyListRepository;
import myapp.bitcoin_analyze.service.CurrencyListService;
import myapp.bitcoin_analyze.service.dto.CurrencyListDTO;
import myapp.bitcoin_analyze.service.mapper.CurrencyListMapper;
import myapp.bitcoin_analyze.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CurrencyList}.
 */
@Service
@Transactional
public class CurrencyListServiceImpl implements CurrencyListService {

    private final Logger log = LoggerFactory.getLogger(CurrencyListServiceImpl.class);

    private final CurrencyListRepository currencyListRepository;

    private final CurrencyListMapper currencyListMapper;

    public CurrencyListServiceImpl(CurrencyListRepository currencyListRepository, CurrencyListMapper currencyListMapper) {
        this.currencyListRepository = currencyListRepository;
        this.currencyListMapper = currencyListMapper;
    }

    @Override
    public CurrencyListDTO save(CurrencyListDTO currencyListDTO) {
        log.debug("Request to save CurrencyList : {}", currencyListDTO);
        CurrencyList currencyList = currencyListMapper.toEntity(currencyListDTO);
        currencyList = currencyListRepository.save(currencyList);
        return currencyListMapper.toDto(currencyList);
    }

    @Override
    public CurrencyListDTO update(CurrencyListDTO currencyListDTO) {
        log.debug("Request to save CurrencyList : {}", currencyListDTO);
        CurrencyList currencyList = currencyListMapper.toEntity(currencyListDTO);
        currencyList.setIsPersisted();
        currencyList = currencyListRepository.save(currencyList);
        return currencyListMapper.toDto(currencyList);
    }

    @Override
    public Optional<CurrencyListDTO> partialUpdate(CurrencyListDTO currencyListDTO) {
        log.debug("Request to partially update CurrencyList : {}", currencyListDTO);

        return currencyListRepository
            .findById(currencyListDTO.getId())
            .map(existingCurrencyList -> {
                currencyListMapper.partialUpdate(existingCurrencyList, currencyListDTO);

                return existingCurrencyList;
            })
            .map(currencyListRepository::save)
            .map(currencyListMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CurrencyListDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CurrencyLists");
        return currencyListRepository.findAll(pageable).map(currencyListMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurrencyListDTO> findAllByIsActiveAndMarketApiId(String isActive,String marketApiId) {
        log.debug("Request to get all CurrencyLists");
        return currencyListMapper.toDto(currencyListRepository.findAllByIsActiveAndMarketApiId(isActive,marketApiId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrencyListDTO> findOne(String id) {
        log.debug("Request to get CurrencyList : {}", id);
        return currencyListRepository.findById(id).map(currencyListMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete CurrencyList : {}", id);
        currencyListRepository.deleteById(id);
    }
}
