package myapp.bitcoin_analyze.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class RequestDTO {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String currencyCode;
    private String timeType;
    private String marketId;

    //Using for Encryptor AES
    private LocalDate requestDate;
    private LocalTime requestTime;

}
