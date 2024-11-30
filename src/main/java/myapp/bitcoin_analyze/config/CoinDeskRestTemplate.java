package myapp.bitcoin_analyze.config;

import org.springframework.web.client.RestTemplate;
public class CoinDeskRestTemplate {

    private static RestTemplate instance;

    private CoinDeskRestTemplate() { }

    public static RestTemplate getInstance() {
        if (instance == null) {
            synchronized (CoinDeskRestTemplate.class) {
                if (instance == null) {
                    instance = new RestTemplate();
                }
            }
        }
        return instance;
    }
}