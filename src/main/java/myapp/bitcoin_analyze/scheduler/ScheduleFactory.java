package myapp.bitcoin_analyze.scheduler;

import myapp.bitcoin_analyze.service.dto.CronScheduleDTO;

public interface ScheduleFactory {
	public void executeSchedule();

	public String getTimeSchedule();

	public void setTimeSchedule(String cronTime);

	public CronScheduleDTO getCronScheduleDTO();

	public void setCronScheduleDTO(CronScheduleDTO cronScheduleDTO);
}
