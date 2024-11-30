package myapp.bitcoin_analyze.web.rest;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * REST controller for managing {@link myapp.bitcoin_analyze.domain.BitcoinExchange}.
 */
@RestController
@RequestMapping("/api")
public class CoinDeskResource {
    private final Logger log = LoggerFactory.getLogger(CoinDeskResource.class);
}
