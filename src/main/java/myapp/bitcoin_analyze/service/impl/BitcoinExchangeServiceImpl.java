package myapp.bitcoin_analyze.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import myapp.bitcoin_analyze.domain.BitcoinExchange;
import myapp.bitcoin_analyze.repository.BitcoinExchangeRepository;
import myapp.bitcoin_analyze.service.BitcoinExchangeService;
import myapp.bitcoin_analyze.service.CurrencyListService;
import myapp.bitcoin_analyze.service.MarketApiService;
import myapp.bitcoin_analyze.service.dto.BitcoinExchangeDTO;
import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import myapp.bitcoin_analyze.service.dto.CurrencyListDTO;
import myapp.bitcoin_analyze.service.mapper.BitcoinExchangeMapper;
import myapp.bitcoin_analyze.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import myapp.bitcoin_analyze.service.dto.MarketApiDTO;
/**
 * Service Implementation for managing {@link BitcoinExchange}.
 */
@Service
@Transactional
public class BitcoinExchangeServiceImpl implements BitcoinExchangeService {

    private final Logger log = LoggerFactory.getLogger(BitcoinExchangeServiceImpl.class);

    @Autowired
    private BitcoinExchangeRepository bitcoinExchangeRepository;

    @Autowired
    private BitcoinExchangeMapper bitcoinExchangeMapper;

    @Autowired
    private MarketApiService marketApiService;

    @Autowired
    private CurrencyListService currencyListService;

    @Autowired
    Environment env;

    @Override
    public BitcoinExchangeDTO save(BitcoinExchangeDTO bitcoinExchangeDTO) {
        log.debug("Request to save BitcoinExchange : {}", bitcoinExchangeDTO);
        BitcoinExchange bitcoinExchange = bitcoinExchangeMapper.toEntity(bitcoinExchangeDTO);
        bitcoinExchange = bitcoinExchangeRepository.save(bitcoinExchange);
        return bitcoinExchangeMapper.toDto(bitcoinExchange);
    }

    @Override
    public List<BitcoinExchangeDTO> saveAll(List<BitcoinExchangeDTO> bitcoinExchangeDTOs) {
        log.debug("Request to save BitcoinExchange : {}", bitcoinExchangeDTOs);
        List<BitcoinExchange> bitcoinExchanges = bitcoinExchangeMapper.toEntity(bitcoinExchangeDTOs);
        bitcoinExchanges = bitcoinExchangeRepository.saveAll(bitcoinExchanges);
        return bitcoinExchangeMapper.toDto(bitcoinExchanges);
    }

    @Override
    public BitcoinExchangeDTO update(BitcoinExchangeDTO bitcoinExchangeDTO) {
        log.debug("Request to save BitcoinExchange : {}", bitcoinExchangeDTO);
        BitcoinExchange bitcoinExchange = bitcoinExchangeMapper.toEntity(bitcoinExchangeDTO);
        bitcoinExchange = bitcoinExchangeRepository.save(bitcoinExchange);
        return bitcoinExchangeMapper.toDto(bitcoinExchange);
    }

    @Override
    public Optional<BitcoinExchangeDTO> partialUpdate(BitcoinExchangeDTO bitcoinExchangeDTO) {
        log.debug("Request to partially update BitcoinExchange : {}", bitcoinExchangeDTO);

        return bitcoinExchangeRepository
            .findById(bitcoinExchangeDTO.getId())
            .map(existingBitcoinExchange -> {
                bitcoinExchangeMapper.partialUpdate(existingBitcoinExchange, bitcoinExchangeDTO);

                return existingBitcoinExchange;
            })
            .map(bitcoinExchangeRepository::save)
            .map(bitcoinExchangeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BitcoinExchangeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BitcoinExchanges");
        return bitcoinExchangeRepository.findAll(pageable).map(bitcoinExchangeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BitcoinExchangeDTO> findOne(Long id) {
        log.debug("Request to get BitcoinExchange : {}", id);
        return bitcoinExchangeRepository.findById(id).map(bitcoinExchangeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete BitcoinExchange : {}", id);
        bitcoinExchangeRepository.deleteById(id);
    }
    @Override
    public List<BitcoinExchangeDTO> fetchCurrentPrice(CronJobDTO cronJobDTO) throws Exception {
        List<MarketApiDTO>  marketApiDTOs = marketApiService.findAllByIsActive(Constants.YES);
        List<BitcoinExchangeDTO> bitcoinExchangeDTOs = new ArrayList<>();
            if (!marketApiDTOs.isEmpty()) {
                for (MarketApiDTO marketApiDTO : marketApiDTOs) {
                    List<CurrencyListDTO> currencyListDTOs = currencyListService.findAllByIsActiveAndMarketApiId(Constants.YES, marketApiDTO.getId());
                    Class<?> mclass = Class.forName(marketApiDTO.getJavaClass());
                    Method method = mclass.getMethod(marketApiDTO.getJavaMethod(), MarketApiDTO.class, List.class, Environment.class, CronJobDTO.class);
                    Object obj = mclass.newInstance();
                    bitcoinExchangeDTOs.addAll((List<BitcoinExchangeDTO>) method.invoke(obj, marketApiDTO, currencyListDTOs, env, cronJobDTO));
                }
                bitcoinExchangeDTOs = saveAll(bitcoinExchangeDTOs);
            }
        return bitcoinExchangeDTOs;
    }

    @Override
    public LocalDate getMinCreatedDate(){
        return bitcoinExchangeRepository.getMinCreatedDate();
    }
}
