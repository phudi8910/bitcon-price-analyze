package myapp.bitcoin_analyze.web.rest;

import myapp.bitcoin_analyze.config.ApiResponse;
import myapp.bitcoin_analyze.repository.BitcoinExchangeRepository;
import myapp.bitcoin_analyze.service.BitcoinExchangeQueryService;
import myapp.bitcoin_analyze.service.BitcoinExchangeService;
import myapp.bitcoin_analyze.service.CoinDeskService;
import myapp.bitcoin_analyze.service.dto.BitcoinExchangeDTO;
import myapp.bitcoin_analyze.service.dto.RequestDTO;
import myapp.bitcoin_analyze.util.Common;
import myapp.bitcoin_analyze.util.Constants;
import myapp.bitcoin_analyze.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import myapp.bitcoin_analyze.service.ValidateService;
/**
 * REST controller for managing {@link myapp.bitcoin_analyze.domain.BitcoinExchange}.
 */
@RestController
@RequestMapping("/api")
public class BitcoinExchangeResource {

    private final Logger log = LoggerFactory.getLogger(BitcoinExchangeResource.class);
    @Autowired
    private BitcoinExchangeService bitcoinExchangeService;

    @Autowired
    private BitcoinExchangeQueryService bitcoinExchangeQueryService;

    @Autowired
    private BitcoinExchangeRepository bitcoinExchangeRepository;

    @Autowired
    private CoinDeskService coinDeskService;

    @Autowired
    private ValidateService validateService;

    @GetMapping("/bitcoin-exchanges")
    public ResponseEntity<List<BitcoinExchangeDTO>> getAllBitcoinExchanges(RequestDTO requestDTO,Pageable pageable){
        Page<BitcoinExchangeDTO> page = bitcoinExchangeQueryService.findByCriteria(requestDTO,pageable);
        return ResponseEntity.ok().body(page.getContent());
    }

    @GetMapping("/bitcoin-exchanges/fetchCurrentPrice")
    public ResponseEntity<?> fetchCurrentPrice(
    ) {
        try {
            List<BitcoinExchangeDTO> bitcoinExchangeDTOs = bitcoinExchangeService.fetchCurrentPrice(null);
            return ResponseEntity.ok().body(bitcoinExchangeDTOs);
        }
        catch (Exception e){
            return new ResponseEntity<>(validateService.responseEntityException(e,-1), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
