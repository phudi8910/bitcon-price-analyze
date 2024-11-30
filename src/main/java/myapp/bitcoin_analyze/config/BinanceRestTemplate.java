package myapp.bitcoin_analyze.config;

import org.springframework.web.client.RestTemplate;
public class BinanceRestTemplate {

    private static RestTemplate instance;

    private BinanceRestTemplate() { }

    public static RestTemplate getInstance() {
        if (instance == null) {
            synchronized (BinanceRestTemplate.class) {
                if (instance == null) {
                    instance = new RestTemplate();
                }
            }
        }
        return instance;
    }
}