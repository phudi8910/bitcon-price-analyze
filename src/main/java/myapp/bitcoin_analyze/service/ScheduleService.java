package myapp.bitcoin_analyze.service;

import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import myapp.bitcoin_analyze.service.dto.CronScheduleDTO;
import myapp.bitcoin_analyze.util.Constants;
import myapp.bitcoin_analyze.util.DateUtils;

public interface ScheduleService {

    public void scheduleFetchCurrentPrice(CronScheduleDTO cronScheduleDTO);

    public void ScheduleBitcoinAnalyzePreviousDay(CronScheduleDTO cronScheduleDTO);

    public void ScheduleBitcoinAnalyzePreviousWeek(CronScheduleDTO cronScheduleDTO);

    public void ScheduleBitcoinAnalyzePreviousMonth(CronScheduleDTO cronScheduleDTO);

    public String stopSchedule();

    public String startSchedule();

    public String listSchedules();


}
