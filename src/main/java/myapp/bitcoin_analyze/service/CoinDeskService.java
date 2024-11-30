package myapp.bitcoin_analyze.service;

import myapp.bitcoin_analyze.config.CoinDeskRestTemplate;
import myapp.bitcoin_analyze.service.dto.BitcoinExchangeDTO;
import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import myapp.bitcoin_analyze.service.dto.CurrencyListDTO;
import myapp.bitcoin_analyze.service.dto.MarketApiDTO;
import myapp.bitcoin_analyze.service.impl.BitcoinExchangeServiceImpl;
import myapp.bitcoin_analyze.util.Common;
import myapp.bitcoin_analyze.util.Constants;
import myapp.bitcoin_analyze.util.DateUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoinDeskService extends IntegrationFactory{

    private final Logger log = LoggerFactory.getLogger(BitcoinExchangeServiceImpl.class);
    @Autowired
    private RestTemplate restTemplate = CoinDeskRestTemplate.getInstance();

    @Autowired
    private CurrencyListService currencyListService;

    @Autowired
    private BitcoinExchangeService bitcoinExchangeService;

    @Autowired
    Environment env;

    @Override
    public List<BitcoinExchangeDTO> fetchCurrentPrice(MarketApiDTO marketApiDTO, List<CurrencyListDTO> currencyListDTOs, Environment environment, CronJobDTO cronJobDTO) throws Exception {
        List<BitcoinExchangeDTO> BitcoinExchangeDTOs = new ArrayList<>();
            ResponseEntity<String> reponseEntity = restTemplate.getForEntity(
                    marketApiDTO.getUrl(), String.class);
            if (reponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                JSONObject responseObject = new JSONObject(reponseEntity.getBody());
                if (responseObject.has("bpi")) {
                    JSONObject bpi = responseObject.getJSONObject("bpi");
                    if (!currencyListDTOs.isEmpty()) {
                        String[] responseFields = environment.getProperty(marketApiDTO.getId() + ".response.field").split(",");
                        String[] responseDatatypes = environment.getProperty(marketApiDTO.getId() + ".response.datatype").split(",");
                        String[] persists = environment.getProperty(marketApiDTO.getId() + ".persist").split(",");
                        Instant currentTimestamp = DateUtils.instantCurrentDateTime();
                        LocalDate currentDate = LocalDate.now();
                        BitcoinExchangeDTO bitcoinExchangeDTO;
                        Long cronJobId = null;
                        if (cronJobDTO != null) {
                            cronJobId = cronJobDTO.getId();
                            cronJobDTO.setLogInfo(Common.stripDescription(reponseEntity.getBody(),499));
                        }

                        for (CurrencyListDTO currencyListDTO : currencyListDTOs) {
                            bitcoinExchangeDTO = BitcoinExchangeDTO.builder()
                                    .marketApi(MarketApiDTO.builder().id(marketApiDTO.getId()).build())
                                    .createdTimeStamp(currentTimestamp)
                                    .createdDate(currentDate)
                                    .cronJobId(cronJobId)
                                    .build();
                            JSONObject currencyObj = bpi.getJSONObject(currencyListDTO.getId());
                            currencyBinding(currencyObj, bitcoinExchangeDTO, responseFields, responseDatatypes, persists);
                            BitcoinExchangeDTOs.add(bitcoinExchangeDTO);
                            bitcoinExchangeDTO = null;
                        }
                    }
                }
            }
        return BitcoinExchangeDTOs;
    }
}