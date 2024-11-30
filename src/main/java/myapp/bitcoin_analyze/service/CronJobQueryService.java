package myapp.bitcoin_analyze.service;

import jakarta.persistence.criteria.Predicate;
import myapp.bitcoin_analyze.domain.CronJob;
import myapp.bitcoin_analyze.repository.CronJobRepository;
import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import myapp.bitcoin_analyze.service.mapper.CronJobMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
public class CronJobQueryService {
    private final Logger log = LoggerFactory.getLogger(CronJobQueryService.class);

    @Autowired
    private CronJobRepository cronJobRepository;

    @Autowired
    private CronJobMapper cronJobMapper;

    @Transactional(readOnly = true)
    public Page<CronJobDTO> findByCriteria(CronJobDTO cronJobDTO, Pageable page) {
        log.debug("find by criteria : {}, page: {}", cronJobDTO, page);
        final Specification<CronJob> specification = createSpecification(cronJobDTO);
        return cronJobRepository.findAll(specification, page).map(cronJobMapper::toDto);
    }

    protected Specification<CronJob> createSpecification(CronJobDTO cronJobDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (cronJobDTO.getJobName() != null) {
                predicates.add(cb.like(cb.upper(root.get("jobName")),
                        "%"+cronJobDTO.getJobName().toUpperCase()+"%"));
            }

            if (cronJobDTO.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), cronJobDTO.getStatus()));
            }

            if (cronJobDTO.getStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), cronJobDTO.getStartTime()));
            }

            if (cronJobDTO.getEndTime() != null) {
                predicates.add(cb.lessThan(root.get("endTime"), cronJobDTO.getEndTime().plus(1L, ChronoUnit.DAYS)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}


