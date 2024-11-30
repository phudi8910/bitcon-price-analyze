package myapp.bitcoin_analyze.service.impl;

import java.util.Optional;
import myapp.bitcoin_analyze.domain.CronJob;
import myapp.bitcoin_analyze.repository.CronJobRepository;
import myapp.bitcoin_analyze.service.CronJobService;
import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import myapp.bitcoin_analyze.service.mapper.CronJobMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CronJob}.
 */
@Service
@Transactional
public class CronJobServiceImpl implements CronJobService {

    private final Logger log = LoggerFactory.getLogger(CronJobServiceImpl.class);

    private final CronJobRepository cronJobRepository;

    private final CronJobMapper cronJobMapper;

    public CronJobServiceImpl(CronJobRepository cronJobRepository, CronJobMapper cronJobMapper) {
        this.cronJobRepository = cronJobRepository;
        this.cronJobMapper = cronJobMapper;
    }

    @Override
    public CronJobDTO save(CronJobDTO cronJobDTO) {
        log.debug("Request to save CronJob : {}", cronJobDTO);
        CronJob cronJob = cronJobMapper.toEntity(cronJobDTO);
        cronJob = cronJobRepository.save(cronJob);
        return cronJobMapper.toDto(cronJob);
    }

    @Override
    public CronJobDTO saveAndFlush(CronJobDTO cronJobDTO) {
        log.debug("Request to saveandFlush CronJob : {}", cronJobDTO);
        CronJob cronJob = cronJobMapper.toEntity(cronJobDTO);
        cronJob = cronJobRepository.saveAndFlush(cronJob);
        return cronJobMapper.toDto(cronJob);
    }

    @Override
    public CronJobDTO update(CronJobDTO cronJobDTO) {
        log.debug("Request to save CronJob : {}", cronJobDTO);
        CronJob cronJob = cronJobMapper.toEntity(cronJobDTO);
        cronJob = cronJobRepository.save(cronJob);
        return cronJobMapper.toDto(cronJob);
    }

    @Override
    public Optional<CronJobDTO> partialUpdate(CronJobDTO cronJobDTO) {
        log.debug("Request to partially update CronJob : {}", cronJobDTO);

        return cronJobRepository
            .findById(cronJobDTO.getId())
            .map(existingCronJob -> {
                cronJobMapper.partialUpdate(existingCronJob, cronJobDTO);

                return existingCronJob;
            })
            .map(cronJobRepository::save)
            .map(cronJobMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CronJobDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CronJobs");
        return cronJobRepository.findAll(pageable).map(cronJobMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CronJobDTO> findOne(Long id) {
        log.debug("Request to get CronJob : {}", id);
        return cronJobRepository.findById(id).map(cronJobMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CronJob : {}", id);
        cronJobRepository.deleteById(id);
    }
}
