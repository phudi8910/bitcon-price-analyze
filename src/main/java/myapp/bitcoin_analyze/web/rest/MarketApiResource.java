package myapp.bitcoin_analyze.web.rest;

import myapp.bitcoin_analyze.service.MarketApiQueryService;
import myapp.bitcoin_analyze.service.dto.MarketApiDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for managing {@link myapp.bitcoin_analyze.domain.MarketApi}.
 */
@RestController
@RequestMapping("/api")
public class MarketApiResource {
    private final Logger log = LoggerFactory.getLogger(MarketApiResource.class);

    @Autowired
    private MarketApiQueryService marketApiQueryService;

    @GetMapping("/market-apis")
    public ResponseEntity<List<MarketApiDTO>> getAllMarketApis(
            MarketApiDTO marketApiDTO, Pageable pageable
    ) {
        log.debug("REST request to get MarketApi Paging by criteria: {}", marketApiDTO);
        Page<MarketApiDTO> page = marketApiQueryService.findByCriteria(marketApiDTO, pageable);
        return ResponseEntity.ok().body(page.getContent());
    }
}
