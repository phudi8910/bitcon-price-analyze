package myapp.bitcoin_analyze.scheduler;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobExecute implements Job {
	private final Logger log = LoggerFactory.getLogger(JobExecute.class);

	@Override
	public void execute(JobExecutionContext context) {
		log.info("REST JobExecute request to get execute : {}", context);
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		ScheduleFactory scheduleFactory = (ScheduleFactory) jobDataMap.get(JobExecute.class.getSimpleName());
		scheduleFactory.executeSchedule();
	}
}
