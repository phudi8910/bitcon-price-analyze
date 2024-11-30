package myapp.bitcoin_analyze.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class RestTemplateLoggingInterceptorIT {
    private  String API ="http://api.coindesk.com/v1/bpi/currentprice.json";

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void restTemplate(){
        restTemplate.exchange(API, HttpMethod.GET,null,Object.class);
    }
}
