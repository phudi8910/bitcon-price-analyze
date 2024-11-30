package myapp.bitcoin_analyze.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.PostLoad;
import myapp.bitcoin_analyze.domain.BitcoinAnalyze;
import myapp.bitcoin_analyze.domain.BitcoinExchange;
import myapp.bitcoin_analyze.repository.BitcoinAnalyzeRepository;
import myapp.bitcoin_analyze.service.BitcoinAnalyzeService;
import myapp.bitcoin_analyze.service.BitcoinExchangeService;
import myapp.bitcoin_analyze.service.ValidateService;
import myapp.bitcoin_analyze.service.dto.BitcoinAnalyzeDTO;
import myapp.bitcoin_analyze.service.dto.BitcoinExchangeDTO;
import myapp.bitcoin_analyze.service.mapper.BitcoinAnalyzeMapper;
import myapp.bitcoin_analyze.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BitcoinAnalyze}.
 */
@Service
@Transactional
public class BitcoinAnalyzeServiceImpl implements BitcoinAnalyzeService {

    private final Logger log = LoggerFactory.getLogger(BitcoinAnalyzeServiceImpl.class);

    @Autowired
    private BitcoinAnalyzeRepository bitcoinAnalyzeRepository;

    @Autowired
    private BitcoinAnalyzeMapper bitcoinAnalyzeMapper;

    @Autowired
    private BitcoinExchangeService bitcoinExchangeService;

    @Autowired
    private ValidateService validateService;

    @Override
    public BitcoinAnalyzeDTO save(BitcoinAnalyzeDTO bitcoinAnalyzeDTO) {
        log.debug("Request to save BitcoinAnalyze : {}", bitcoinAnalyzeDTO);
        BitcoinAnalyze bitcoinAnalyze = bitcoinAnalyzeMapper.toEntity(bitcoinAnalyzeDTO);
        bitcoinAnalyze = bitcoinAnalyzeRepository.save(bitcoinAnalyze);
        return bitcoinAnalyzeMapper.toDto(bitcoinAnalyze);
    }

    @Override
    public BitcoinAnalyzeDTO update(BitcoinAnalyzeDTO bitcoinAnalyzeDTO) {
        log.debug("Request to save BitcoinAnalyze : {}", bitcoinAnalyzeDTO);
        BitcoinAnalyze bitcoinAnalyze = bitcoinAnalyzeMapper.toEntity(bitcoinAnalyzeDTO);
        bitcoinAnalyze = bitcoinAnalyzeRepository.save(bitcoinAnalyze);
        return bitcoinAnalyzeMapper.toDto(bitcoinAnalyze);
    }

    @Override
    public Optional<BitcoinAnalyzeDTO> partialUpdate(BitcoinAnalyzeDTO bitcoinAnalyzeDTO) {
        log.debug("Request to partially update BitcoinAnalyze : {}", bitcoinAnalyzeDTO);

        return bitcoinAnalyzeRepository
            .findById(bitcoinAnalyzeDTO.getId())
            .map(existingBitcoinAnalyze -> {
                bitcoinAnalyzeMapper.partialUpdate(existingBitcoinAnalyze, bitcoinAnalyzeDTO);

                return existingBitcoinAnalyze;
            })
            .map(bitcoinAnalyzeRepository::save)
            .map(bitcoinAnalyzeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BitcoinAnalyzeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BitcoinAnalyzes");
        return bitcoinAnalyzeRepository.findAll(pageable).map(bitcoinAnalyzeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BitcoinAnalyzeDTO> findOne(Long id) {
        log.debug("Request to get BitcoinAnalyze : {}", id);
        return bitcoinAnalyzeRepository.findById(id).map(bitcoinAnalyzeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete BitcoinAnalyze : {}", id);
        bitcoinAnalyzeRepository.deleteById(id);
    }

    //@Override
    public void deleteByCreatedDateBetweenAndTimeType(LocalDate createdDateFrom,LocalDate createdDateTo,String timeType) {
        log.debug("Request to delete BitcoinAnalyze ByCreatedDate : {},{},{}", createdDateFrom,createdDateTo,timeType);
        bitcoinAnalyzeRepository.deleteByCreatedDateBetweenAndTimeType(createdDateFrom,createdDateTo,timeType);
    }

    public void deleteByCreatedDateBetweenAndTimeTypeAndCurrencyCode(LocalDate createdDateFrom,LocalDate createdDateTo,String timeType,String currencyCode) {
        log.debug("Request to delete BitcoinAnalyze ByCreatedDate : {},{},{}", createdDateFrom,createdDateTo,timeType);
        bitcoinAnalyzeRepository.deleteByCreatedDateBetweenAndTimeTypeAndCurrencyCode(createdDateFrom,createdDateTo,timeType,currencyCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BitcoinAnalyzeDTO> preparationAnalyzeByTimeType(LocalDate fromDate, LocalDate toDate, String timeType,String currencyCode) throws Exception {
        log.debug("Request to get all preparationAnalyzeByTimeType");
        List<BitcoinAnalyze> bitcoinAnalyzes = bitcoinAnalyzeRepository.preparationAnalyzeByTimeType(fromDate,toDate,timeType,currencyCode);
        List<BitcoinAnalyzeDTO> bitcoinAnalyzeDTO = bitcoinAnalyzeMapper.toDto(bitcoinAnalyzes);
        if(!bitcoinAnalyzeDTO.isEmpty()){
            bitcoinAnalyzeDTO.stream().forEach(m -> m.setId(null));
        }
        return bitcoinAnalyzeDTO;
    }

    @Override
    public List<BitcoinAnalyzeDTO> saveAll(List<BitcoinAnalyzeDTO> bitcoinAnalyzeDTOs) {
        log.debug("Request to save all bitcoinAnalyzeDTOs : {}", bitcoinAnalyzeDTOs);
        List<BitcoinAnalyze> bitcoinAnalyzes = bitcoinAnalyzeMapper.toEntity(bitcoinAnalyzeDTOs);
        bitcoinAnalyzes = bitcoinAnalyzeRepository.saveAll(bitcoinAnalyzes);
        return bitcoinAnalyzeMapper.toDto(bitcoinAnalyzes);
    }

    @Override
    public List<BitcoinAnalyzeDTO> processAnalyzeByTimeType(String timeType, String currencyCode) throws Exception {
        List<BitcoinAnalyzeDTO> bitcoinAnalyzeDTO = new ArrayList<>();
        List<BitcoinAnalyzeDTO> bitcoinAnalyzeDTOAll = new ArrayList<>();
        String[] timeTypes;
        if (timeType == null) {
            timeTypes = new String[]{Constants.DAILY, Constants.WEEKLY, Constants.MONTHLY};
        } else {
            timeTypes = new String[]{timeType};
        }
        for (String processTimeType : timeTypes){
            switch (processTimeType) {
                case Constants.DAILY:
                    LocalDate previousDate = LocalDate.now().minusDays(1L);
                    bitcoinAnalyzeDTO = preparationAnalyzeByTimeType(previousDate, previousDate, Constants.DAILY, currencyCode);
                    if (currencyCode != null) {
                        deleteByCreatedDateBetweenAndTimeTypeAndCurrencyCode(previousDate, previousDate, Constants.DAILY, currencyCode);
                    } else {
                        deleteByCreatedDateBetweenAndTimeType(previousDate, previousDate, Constants.DAILY);
                    }
                    bitcoinAnalyzeDTO = saveAll(bitcoinAnalyzeDTO);
                    bitcoinAnalyzeDTOAll.addAll(bitcoinAnalyzeDTO);
                    break;
                case Constants.WEEKLY:
                    LocalDate[] previousWeekday = getPreviousWeekday(LocalDate.now());
                    bitcoinAnalyzeDTO = preparationAnalyzeByTimeType(previousWeekday[0], previousWeekday[1], Constants.WEEKLY, currencyCode);
                    if (currencyCode != null) {
                        deleteByCreatedDateBetweenAndTimeTypeAndCurrencyCode(previousWeekday[0], previousWeekday[1], Constants.WEEKLY, currencyCode);
                    } else {
                        deleteByCreatedDateBetweenAndTimeType(previousWeekday[0], previousWeekday[1], Constants.WEEKLY);
                    }
                    bitcoinAnalyzeDTO = saveAll(bitcoinAnalyzeDTO);
                    //bitcoinAnalyzeDTO.stream().forEach(m -> bitcoinAnalyzeDTOAll.add(m));
                    bitcoinAnalyzeDTOAll.addAll(bitcoinAnalyzeDTO);
                    break;

                case Constants.MONTHLY:
                    LocalDate previousMonth = LocalDate.now().minusMonths(1L);
                    LocalDate previousMonthFrom = LocalDate.of(previousMonth.getYear(), previousMonth.getMonthValue(), 1);
                    LocalDate previousMonthTo = LocalDate.of(previousMonth.getYear(), previousMonth.getMonthValue(), previousMonth.lengthOfMonth());
                    bitcoinAnalyzeDTO = preparationAnalyzeByTimeType(previousMonthFrom, previousMonthTo, Constants.MONTHLY, currencyCode);
                    if (currencyCode != null) {
                        deleteByCreatedDateBetweenAndTimeTypeAndCurrencyCode(previousMonthFrom, previousMonthTo, Constants.MONTHLY, currencyCode);
                    } else {
                        deleteByCreatedDateBetweenAndTimeType(previousMonthFrom, previousMonthTo, Constants.MONTHLY);
                    }

                    bitcoinAnalyzeDTO = saveAll(bitcoinAnalyzeDTO);
                    bitcoinAnalyzeDTOAll.addAll(bitcoinAnalyzeDTO);
                    break;
            }
        }
        return bitcoinAnalyzeDTOAll;
    }

    @Override
    public List<BitcoinAnalyzeDTO> queryAnalyzeByTimeType(String timeType, String currencyCode,LocalDate fromDate, LocalDate toDate) throws Exception {
        List<BitcoinAnalyzeDTO> bitcoinAnalyzeDTO = new ArrayList<>();
        bitcoinAnalyzeDTO = preparationAnalyzeByTimeType(fromDate,toDate, timeType,currencyCode);
        return bitcoinAnalyzeDTO;
    }

    public static LocalDate[] getPreviousWeekday(LocalDate date) throws Exception{
        if (date == null) {
            date = LocalDate.now();
        }
        LocalDate end = null;
        if (date.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            end = date;
        } else {
            end = date.minusDays(date.getDayOfWeek().getValue());
        }
        LocalDate begin = end.minusDays(Constants.DAYS_OF_WEEK - 1);
        return new LocalDate[]{ begin, end };
    }

    @PostConstruct
    @Override
    public List<BitcoinAnalyzeDTO> initBitcoinAnalyze(){
        List<BitcoinAnalyzeDTO> bitcoinAnalyzeDTO = new ArrayList<>();
        try{
            LocalDate minCreatedDate = bitcoinExchangeService.getMinCreatedDate();
            LocalDate minCreatedDateForDaily = LocalDate.of(minCreatedDate.getYear(),minCreatedDate.getMonthValue(),minCreatedDate.getDayOfMonth());
            LocalDate previousDateForDaily = LocalDate.now().minusDays(1L);
            while(minCreatedDateForDaily.isBefore(previousDateForDaily) ){
                bitcoinAnalyzeDTO = preparationAnalyzeByTimeType(minCreatedDateForDaily,minCreatedDateForDaily, Constants.DAILY,null);
                deleteByCreatedDateBetweenAndTimeType(minCreatedDateForDaily,minCreatedDateForDaily,Constants.DAILY);
                saveAll(bitcoinAnalyzeDTO);
                minCreatedDateForDaily =minCreatedDateForDaily.plusDays(1L);
            }

            LocalDate minCreatedDateForWeekly = LocalDate.of(minCreatedDate.getYear(),minCreatedDate.getMonthValue(),minCreatedDate.getDayOfMonth());
            LocalDate[] previousDateForWeekly = getPreviousWeekday(null);
            while(minCreatedDateForWeekly.isBefore(previousDateForWeekly[1]) ){
                previousDateForWeekly = getPreviousWeekday(previousDateForWeekly[0]);
                bitcoinAnalyzeDTO = preparationAnalyzeByTimeType(previousDateForWeekly[0],previousDateForWeekly[1], Constants.WEEKLY,null);
                deleteByCreatedDateBetweenAndTimeType(previousDateForWeekly[0],previousDateForWeekly[1],Constants.WEEKLY);
                saveAll(bitcoinAnalyzeDTO);
            }

            //LocalDate minCreatedDateForMonthly = LocalDate.of(2024,5,5);
            LocalDate minCreatedDateForMonthly = LocalDate.of(minCreatedDate.getYear(),minCreatedDate.getMonthValue(),minCreatedDate.getDayOfMonth());
            LocalDate previousDateForMonthlyFrom = LocalDate.of(LocalDate.now().minusMonths(2).getYear(),LocalDate.now().minusMonths(2).getMonthValue(),1);
            LocalDate previousDateForMonthlyTo = LocalDate.of(LocalDate.now().minusMonths(2).getYear(),LocalDate.now().minusMonths(2).getMonthValue(),LocalDate.now().minusMonths(2).lengthOfMonth());
            while(minCreatedDateForMonthly.isBefore(previousDateForMonthlyTo) ){
                bitcoinAnalyzeDTO = preparationAnalyzeByTimeType(previousDateForMonthlyFrom,previousDateForMonthlyTo, Constants.MONTHLY,null);
                deleteByCreatedDateBetweenAndTimeType(previousDateForMonthlyFrom,previousDateForMonthlyTo,Constants.MONTHLY);
                saveAll(bitcoinAnalyzeDTO);
                previousDateForMonthlyFrom = LocalDate.of(previousDateForMonthlyTo.minusMonths(1).getYear(),previousDateForMonthlyTo.minusMonths(1).getMonthValue(),1);
                previousDateForMonthlyTo = LocalDate.of(previousDateForMonthlyTo.minusMonths(1).getYear(),previousDateForMonthlyTo.minusMonths(1).getMonthValue(),previousDateForMonthlyFrom.lengthOfMonth());
            }
        }
        catch (Exception e){
            validateService.responseEntityException(e,-1);
        }
        return bitcoinAnalyzeDTO;
    }
}
