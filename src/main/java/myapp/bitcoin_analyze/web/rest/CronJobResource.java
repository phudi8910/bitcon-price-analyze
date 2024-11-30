package myapp.bitcoin_analyze.web.rest;

import java.util.List;
import myapp.bitcoin_analyze.service.CronJobQueryService;
import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST controller for managing @link myapp.bitcoin_analyze.service.CronJob.
 */
@RestController
@RequestMapping("/api")
public class CronJobResource {
    private final Logger log = LoggerFactory.getLogger(CronJobResource.class);

    @Autowired
    private CronJobQueryService cronJobQueryService;

    @GetMapping("/cron-jobs")
    public ResponseEntity<List<CronJobDTO>> getAllCronJobs(
            CronJobDTO cronJobDTO, Pageable pageable
    ) {
        log.debug("REST request to get CronJobs Paging by criteria: {}", cronJobDTO);
        Page<CronJobDTO> page = cronJobQueryService.findByCriteria(cronJobDTO, pageable);
        return ResponseEntity.ok().body(page.getContent());
    }
}
