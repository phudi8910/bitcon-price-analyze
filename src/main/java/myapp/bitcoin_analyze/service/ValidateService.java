package myapp.bitcoin_analyze.service;

import myapp.bitcoin_analyze.config.ApiResponse;
import myapp.bitcoin_analyze.repository.CurrencyListRepository;
import myapp.bitcoin_analyze.service.dto.RequestDTO;
import myapp.bitcoin_analyze.util.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
public class ValidateService {
    private final Logger log = LoggerFactory.getLogger(ValidateService.class);

    @Autowired
    private CurrencyListRepository currencyListRepository;

    public ApiResponse<String> responseEntityException(Exception e,int maxLength){
        String messgage;
        if (e.getMessage()!=null){
            messgage=e.getMessage();
        }
        else{
            messgage=e.getCause().getMessage() ;
        }
        log.error(messgage);
        //e.printStackTrace();
        return ApiResponse.error(Common.stripDescription(messgage,maxLength));
    }

    public ApiResponse<RequestDTO> requestValidate(RequestDTO requestDTO, MultipartFile file) {
        if (requestDTO.getCurrencyCode() != null) {
            String currencyCode;
            currencyCode = requestDTO.getCurrencyCode();
            if (!currencyListRepository.existsById(currencyCode)) {
                return ApiResponse.error("Currency "+currencyCode + " is not found");
            }
        }
        if (requestDTO.getMarketId() != null) {
            String marketId;
            marketId = requestDTO.getMarketId();
            if (!currencyListRepository.existsByMarketApiId(marketId)) {
                return ApiResponse.error("Market "+marketId + " is not found");
            }
        }

        if (requestDTO.getFromDate() != null) {
            LocalDate currentDate = LocalDate.now();
            if (requestDTO.getFromDate().isAfter(currentDate)) {
                return ApiResponse.error("FromDate "+requestDTO.getFromDate() + " must less than "+currentDate);
            }
        }

        if (requestDTO.getToDate() != null) {
            LocalDate currentDate = LocalDate.now();
            if (requestDTO.getToDate().isAfter(currentDate)) {
                return ApiResponse.error("ToDate "+requestDTO.getToDate() + " must less than or equal "+currentDate);
            }
        }

        if (requestDTO.getFromDate() != null & requestDTO.getToDate() != null) {
            if (requestDTO.getFromDate().isAfter(requestDTO.getToDate())) {
                return ApiResponse.error("FromDate "+requestDTO.getFromDate() + " must less than ToDate "+requestDTO.getToDate());
            }
        }

        return ApiResponse.success(requestDTO);
    }
}
