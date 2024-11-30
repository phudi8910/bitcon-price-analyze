package myapp.bitcoin_analyze.service.mapper;

import myapp.bitcoin_analyze.domain.CronJob;
import myapp.bitcoin_analyze.domain.CronSchedule;
import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import myapp.bitcoin_analyze.service.dto.CronScheduleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CronJob} and its DTO {@link CronJobDTO}.
 */
@Mapper(componentModel = "spring")
public interface CronJobMapper extends EntityMapper<CronJobDTO, CronJob> {
    @Mapping(target = "cronSchedule", source = "cronSchedule", qualifiedByName = "cronScheduleId")
    CronJobDTO toDto(CronJob s);

    @Named("cronScheduleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CronScheduleDTO toDtoCronScheduleId(CronSchedule cronSchedule);
}
