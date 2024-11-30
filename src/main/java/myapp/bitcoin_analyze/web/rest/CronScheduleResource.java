package myapp.bitcoin_analyze.web.rest;

import java.util.List;
import myapp.bitcoin_analyze.service.CronScheduleQueryService;
import myapp.bitcoin_analyze.service.dto.CronScheduleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing @link myapp.bitcoin_analyze.service.CronSchedule}.
 */
@RestController
@RequestMapping("/api")
public class CronScheduleResource {
    private final Logger log = LoggerFactory.getLogger(CronScheduleResource.class);

    @Autowired
    private CronScheduleQueryService cronScheduleQueryService;

    @GetMapping("/cron-schedules")
    public ResponseEntity<List<CronScheduleDTO>> getAllCronSchedules(
            CronScheduleDTO cronScheduleDTO, Pageable pageable
    ) {
        log.debug("REST request to get cronSchedules Paging by criteria: {}", cronScheduleDTO);
        Page<CronScheduleDTO> page = cronScheduleQueryService.findByCriteria(cronScheduleDTO, pageable);
        return ResponseEntity.ok().body(page.getContent());
    }
}
