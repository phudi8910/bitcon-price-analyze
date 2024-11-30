package myapp.bitcoin_analyze.scheduler;

import myapp.bitcoin_analyze.service.ScheduleService;
import myapp.bitcoin_analyze.service.dto.CronScheduleDTO;

public class ScheduleFetchCurrentPrice implements ScheduleFactory {
    private String cronTime;
    private ScheduleService scheduleService;
    private CronScheduleDTO cronScheduleDTO;

    public ScheduleFetchCurrentPrice(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public void executeSchedule() {
        scheduleService.scheduleFetchCurrentPrice(cronScheduleDTO);
    }

    @Override
    public String getTimeSchedule() {
        return this.cronTime;
    }

    @Override
    public void setTimeSchedule(String cronTime) {
        this.cronTime = cronTime;
    }

    @Override
    public CronScheduleDTO getCronScheduleDTO() {
        return this.cronScheduleDTO;
    }

    @Override
    public void setCronScheduleDTO(CronScheduleDTO cronScheduleDTO) {
        this.cronScheduleDTO = cronScheduleDTO;
    }
}
