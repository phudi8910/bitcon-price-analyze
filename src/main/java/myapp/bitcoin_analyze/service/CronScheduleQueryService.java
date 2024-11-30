package myapp.bitcoin_analyze.service;

import jakarta.persistence.criteria.Predicate;
import myapp.bitcoin_analyze.domain.CronSchedule;
import myapp.bitcoin_analyze.repository.CronScheduleRepository;
import myapp.bitcoin_analyze.service.dto.CronScheduleDTO;
import myapp.bitcoin_analyze.service.mapper.CronScheduleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
public class CronScheduleQueryService {
    private final Logger log = LoggerFactory.getLogger(CronScheduleQueryService.class);

    @Autowired
    private CronScheduleRepository cronScheduleRepository;

    @Autowired
    private CronScheduleMapper cronScheduleMapper;

    @Transactional(readOnly = true)
    public Page<CronScheduleDTO> findByCriteria(CronScheduleDTO cronScheduleDTO, Pageable page) {
        log.debug("find by criteria : {}, page: {}", cronScheduleDTO, page);
        final Specification<CronSchedule> specification = createSpecification(cronScheduleDTO);
        return cronScheduleRepository.findAll(specification, page).map(cronScheduleMapper::toDto);
    }

    protected Specification<CronSchedule> createSpecification(CronScheduleDTO cronScheduleDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (cronScheduleDTO.getJobName() != null) {
                predicates.add(cb.like(cb.upper(root.get("jobName")),
                        "%"+cronScheduleDTO.getJobName().toUpperCase()+"%"));
            }

            if (cronScheduleDTO.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), cronScheduleDTO.getIsActive()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}


