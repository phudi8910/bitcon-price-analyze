package myapp.bitcoin_analyze.web.rest;

import java.time.LocalDate;
import java.util.*;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import myapp.bitcoin_analyze.config.ApiResponse;
import myapp.bitcoin_analyze.config.Encryptor;
import myapp.bitcoin_analyze.domain.BitcoinAnalyze;
import myapp.bitcoin_analyze.repository.BitcoinAnalyzeRepository;
import myapp.bitcoin_analyze.repository.CurrencyListRepository;
import myapp.bitcoin_analyze.service.BitcoinAnalyzeQueryService;
import myapp.bitcoin_analyze.service.BitcoinAnalyzeService;
import myapp.bitcoin_analyze.service.dto.BitcoinAnalyzeDTO;
import myapp.bitcoin_analyze.service.dto.RequestDTO;
import myapp.bitcoin_analyze.util.Common;
import myapp.bitcoin_analyze.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import myapp.bitcoin_analyze.service.ValidateService;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
/**
 * REST controller for managing {@link BitcoinAnalyze}.
 */
@RestController
@RequestMapping("/api")
public class BitcoinAnalyzeResource {

    private final Logger log = LoggerFactory.getLogger(BitcoinAnalyzeResource.class);

    private static final String ENTITY_NAME = "bitcoinAnalyze";

    @Autowired
    private BitcoinAnalyzeService bitcoinAnalyzeService;

    @Autowired
    private BitcoinAnalyzeQueryService bitcoinAnalyzeQueryService;

    @Autowired
    private BitcoinAnalyzeRepository bitcoinAnalyzeRepository;

    @Autowired
    Encryptor encryptor;

    @Autowired
    private ValidateService validateService;

    @Autowired
    private CurrencyListRepository currencyListRepository;

    @PostMapping("/bitcoin-analyzes/process-analyze")
    public ResponseEntity<?> processBitcoinAnalyzeManual(
            //@RequestBody RequestDTO requestDTO
    @RequestBody(description = "process BitcoinAnalyze previous Day/Week/Month by manual" +
            ",param RequestDTO is optional", required = false,
            content = @Content(
                    schema=@Schema(implementation = RequestDTO.class))
            )RequestDTO requestDTO )
    {
        try{
            ApiResponse<RequestDTO> requestValidate = validateService.requestValidate(requestDTO, null);
            if(!requestValidate.isSuccess()){
                return new ResponseEntity<>(requestValidate, HttpStatus.BAD_REQUEST);
            }
            List<BitcoinAnalyzeDTO> bitcoinAnalyzeDTOs = bitcoinAnalyzeService.processAnalyzeByTimeType(requestDTO.getTimeType(), requestDTO.getCurrencyCode());
            return ResponseEntity.ok().body(bitcoinAnalyzeDTOs);
        }
        catch(Exception e){
            return new ResponseEntity<>(validateService.responseEntityException(e,-1), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/bitcoin-analyzes/process-analyze-aes")
    public ResponseEntity<?> processBitcoinAnalyzeManualAes(@RequestBody(description = "process BitcoinAnalyze with AES previous Day/Week/Month by manual" +
            ", param RequestDTO is optional, requestDate is required, requestTime is required, signature on requestHeader is required", required = false,
            content = @Content(
                    schema=@Schema(implementation = RequestDTO.class))
    )RequestDTO requestDTO,
        @RequestHeader HttpHeaders headers){
        List<BitcoinAnalyzeDTO> bitcoinAnalyzeDTOs = new ArrayList<>();
        try {
            ApiResponse<RequestDTO> requestValidate = validateService.requestValidate(requestDTO, null);
            if(!requestValidate.isSuccess()){
                return new ResponseEntity<>(requestValidate, HttpStatus.BAD_REQUEST);
            }
            String signature = headers.get("signature").get(0);
            RequestDTO requestAuthDTO=null;
            if (signature != null) {
                requestAuthDTO=encryptor.isAesMessageValid(signature);
                if(requestAuthDTO!=null /*and compare some value between requestAuthDTO and RequestDTO*/){
                    bitcoinAnalyzeDTOs = bitcoinAnalyzeService.processAnalyzeByTimeType(requestDTO.getTimeType(), requestDTO.getCurrencyCode());
                }
                else{
                    return new ResponseEntity<>(ApiResponse.error("Authentication was invalid"), HttpStatus.BAD_REQUEST);
                }
            }
            else{
                return new ResponseEntity<>(ApiResponse.error("Authentication is missing"), HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok().body(bitcoinAnalyzeDTOs);
        }
        catch(Exception e){
            return new ResponseEntity<>(validateService.responseEntityException(e,-1), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bitcoin-analyzes/general-data")
    public ResponseEntity<?> getAllGeneralData(
            RequestDTO requestDTO,Pageable pageable
    ) {
        log.debug("REST request to get BitcoinAnalyzes general-data Paging by criteria: {}", requestDTO);
        try {
            ApiResponse<RequestDTO> requestValidate = validateService.requestValidate(requestDTO, null);
            if(!requestValidate.isSuccess()){
                return new ResponseEntity<>(requestValidate, HttpStatus.BAD_REQUEST);
            }
            LocalDate currentDate = LocalDate.now();
            requestDTO.setTimeType(Constants.DAILY);
            requestDTO.setToDate(currentDate.minusDays(1L));
            if(requestDTO.getFromDate()==null){
                requestDTO.setFromDate(currentDate.minusMonths(1L));
            }
            Page<BitcoinAnalyzeDTO> bitcoinAnalyzeDTOPrevious = bitcoinAnalyzeQueryService.findByCriteria(requestDTO, pageable);
            List<BitcoinAnalyzeDTO> bitcoinAnalyzeDTOs = bitcoinAnalyzeService.queryAnalyzeByTimeType(requestDTO.getTimeType(), requestDTO.getCurrencyCode(), currentDate, currentDate);
            bitcoinAnalyzeDTOs.addAll(bitcoinAnalyzeDTOPrevious.getContent());
            bitcoinAnalyzeDTOs.sort(Comparator.comparing(BitcoinAnalyzeDTO::getCreatedDate, Comparator.reverseOrder()));
            return ResponseEntity.ok().body(bitcoinAnalyzeDTOs);
        }
        catch (Exception e){
            return new ResponseEntity<>(validateService.responseEntityException(e,-1), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bitcoin-analyzes/historical-data")
    public ResponseEntity<?> getAllHistoricalData(
            RequestDTO requestDTO,Pageable pageable
    ) {
        log.debug("REST request to get BitcoinAnalyzes Paging by criteria: {}", requestDTO);
        ApiResponse<RequestDTO> requestValidate = validateService.requestValidate(requestDTO, null);
        if(!requestValidate.isSuccess()){
            return new ResponseEntity<>(requestValidate, HttpStatus.BAD_REQUEST);
        }
        Page<BitcoinAnalyzeDTO> page = bitcoinAnalyzeQueryService.findByCriteria(requestDTO, pageable);
        return ResponseEntity.ok().body(page.getContent());
    }
}
