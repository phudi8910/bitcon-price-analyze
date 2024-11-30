package myapp.bitcoin_analyze.service;

import myapp.bitcoin_analyze.config.BinanceRestTemplate;
import myapp.bitcoin_analyze.service.dto.BitcoinExchangeDTO;
import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import myapp.bitcoin_analyze.service.dto.CurrencyListDTO;
import myapp.bitcoin_analyze.service.dto.MarketApiDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BinanceService extends IntegrationFactory{
    @Autowired
    private RestTemplate restTemplate = BinanceRestTemplate.getInstance();
    @Override
    public List<BitcoinExchangeDTO> fetchCurrentPrice(MarketApiDTO marketApiDTO, List<CurrencyListDTO> currencyListDTOs, Environment environment, CronJobDTO cronJobDTO) throws Exception {
        ResponseEntity<String> reponseEntity = restTemplate.getForEntity(
                marketApiDTO.getUrl(), String.class);
        List<BitcoinExchangeDTO> BitcoinExchangeDTOs = new ArrayList<>();
        if (reponseEntity.getStatusCode().equals(HttpStatus.OK)) {
            //Implement code logic for Binance data response
        }
        return BitcoinExchangeDTOs;
    }
}