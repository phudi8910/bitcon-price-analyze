package myapp.bitcoin_analyze.service.mapper;

import myapp.bitcoin_analyze.domain.CronSchedule;
import myapp.bitcoin_analyze.service.dto.CronScheduleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CronSchedule} and its DTO {@link CronScheduleDTO}.
 */
@Mapper(componentModel = "spring")
public interface CronScheduleMapper extends EntityMapper<CronScheduleDTO, CronSchedule> {}
