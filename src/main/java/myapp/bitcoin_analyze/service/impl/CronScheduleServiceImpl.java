package myapp.bitcoin_analyze.service.impl;

import java.util.List;
import java.util.Optional;
import myapp.bitcoin_analyze.domain.CronSchedule;
import myapp.bitcoin_analyze.repository.CronScheduleRepository;
import myapp.bitcoin_analyze.service.CronScheduleService;
import myapp.bitcoin_analyze.service.dto.CronScheduleDTO;
import myapp.bitcoin_analyze.service.dto.CurrencyListDTO;
import myapp.bitcoin_analyze.service.mapper.CronScheduleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CronSchedule}.
 */
@Service
@Transactional
public class CronScheduleServiceImpl implements CronScheduleService {

    private final Logger log = LoggerFactory.getLogger(CronScheduleServiceImpl.class);

    private final CronScheduleRepository cronScheduleRepository;

    private final CronScheduleMapper cronScheduleMapper;

    public CronScheduleServiceImpl(CronScheduleRepository cronScheduleRepository, CronScheduleMapper cronScheduleMapper) {
        this.cronScheduleRepository = cronScheduleRepository;
        this.cronScheduleMapper = cronScheduleMapper;
    }

    @Override
    public CronScheduleDTO save(CronScheduleDTO cronScheduleDTO) {
        log.debug("Request to save CronSchedule : {}", cronScheduleDTO);
        CronSchedule cronSchedule = cronScheduleMapper.toEntity(cronScheduleDTO);
        cronSchedule = cronScheduleRepository.save(cronSchedule);
        return cronScheduleMapper.toDto(cronSchedule);
    }

    @Override
    public CronScheduleDTO update(CronScheduleDTO cronScheduleDTO) {
        log.debug("Request to save CronSchedule : {}", cronScheduleDTO);
        CronSchedule cronSchedule = cronScheduleMapper.toEntity(cronScheduleDTO);
        cronSchedule = cronScheduleRepository.save(cronSchedule);
        return cronScheduleMapper.toDto(cronSchedule);
    }

    @Override
    public Optional<CronScheduleDTO> partialUpdate(CronScheduleDTO cronScheduleDTO) {
        log.debug("Request to partially update CronSchedule : {}", cronScheduleDTO);

        return cronScheduleRepository
            .findById(cronScheduleDTO.getId())
            .map(existingCronSchedule -> {
                cronScheduleMapper.partialUpdate(existingCronSchedule, cronScheduleDTO);

                return existingCronSchedule;
            })
            .map(cronScheduleRepository::save)
            .map(cronScheduleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CronScheduleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CronSchedules");
        return cronScheduleRepository.findAll(pageable).map(cronScheduleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CronScheduleDTO> findOne(Long id) {
        log.debug("Request to get CronSchedule : {}", id);
        return cronScheduleRepository.findById(id).map(cronScheduleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CronSchedule : {}", id);
        cronScheduleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CronScheduleDTO> findAllByIsActiveOrderByIdAsc(String isActive) {
        log.debug("Request to get all CronScheduleDTO findAllByIsActive");
        return cronScheduleMapper.toDto(cronScheduleRepository.findAllByIsActiveOrderByIdAsc(isActive));
    }
}
