package myapp.bitcoin_analyze.service.impl;

import myapp.bitcoin_analyze.domain.CronSchedule;
import myapp.bitcoin_analyze.scheduler.*;
import myapp.bitcoin_analyze.service.*;
import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import myapp.bitcoin_analyze.service.dto.CronScheduleDTO;
import myapp.bitcoin_analyze.util.Common;
import myapp.bitcoin_analyze.util.DateUtils;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;

import java.util.*;
import java.util.stream.Collectors;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import myapp.bitcoin_analyze.util.Constants;
import myapp.bitcoin_analyze.service.ValidateService;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);

    private ScheduledAnnotationBeanPostProcessor postProcessor;

    private Scheduler scheduler;

    private Environment environment;

    private BitcoinExchangeService bitcoinExchangeService;

    private CronScheduleService cronScheduleService;

    private CronJobService cronJobService;

    private BitcoinAnalyzeService bitcoinAnalyzeService;

    private ValidateService validateService;

    @Value("${spring.jpa.properties.hibernate.jdbc.time_zone}")
    private String timeZone;

    public ScheduleServiceImpl(
            @Lazy ScheduledAnnotationBeanPostProcessor postProcessor,
            @Lazy Environment environment,
            @Lazy Scheduler scheduler,
            @Lazy BitcoinExchangeService bitcoinExchangeService,
            @Lazy CronScheduleService cronScheduleService,
            @Lazy CronJobService cronJobService,
            @Lazy BitcoinAnalyzeService bitcoinAnalyzeService,
            @Lazy ValidateService validateService
    ) {
        this.postProcessor = postProcessor;
        this.environment = environment;
        this.scheduler = scheduler;
        this.bitcoinExchangeService=bitcoinExchangeService;
        this.cronScheduleService=cronScheduleService;
        this.cronJobService=cronJobService;
        this.bitcoinAnalyzeService=bitcoinAnalyzeService;
        this.validateService=validateService;
    }

    @Override
    public void scheduleFetchCurrentPrice(CronScheduleDTO cronScheduleDTO) {
        CronJobDTO cronJobDTO = new CronJobDTO(cronScheduleDTO);
        try {
            cronJobDTO = cronJobService.saveAndFlush(cronJobDTO);
            bitcoinExchangeService.fetchCurrentPrice(cronJobDTO);
            cronJobDTO.setEndTime(DateUtils.instantCurrentDateTime());
            cronJobDTO.setStatus(Constants.CRON_JOB_STATUS.COMPLETE.getKey());
            cronJobService.update(cronJobDTO);
        }
        catch (Exception e){
            cronJobDTO.setStatus(Constants.CRON_JOB_STATUS.FAILED.getKey());
            cronJobDTO.setLogInfo(validateService.responseEntityException(e,499).getMessage());
            cronJobDTO.setEndTime(DateUtils.instantCurrentDateTime());
            cronJobService.update(cronJobDTO);
        }
    }

    @Override
    public void ScheduleBitcoinAnalyzePreviousDay(CronScheduleDTO cronScheduleDTO) {
        CronJobDTO cronJobDTO = new CronJobDTO(cronScheduleDTO);
        try {
            cronJobDTO = cronJobService.saveAndFlush(cronJobDTO);
            //Becasuse I have not had must time, so I didn't apply insertion to bitcoinAnalyze.cron_job_id for clarify the record belong to which cron_job
            bitcoinAnalyzeService.processAnalyzeByTimeType(Constants.DAILY,null);
            cronJobDTO.setEndTime(DateUtils.instantCurrentDateTime());
            cronJobDTO.setStatus(Constants.CRON_JOB_STATUS.COMPLETE.getKey());
            cronJobService.update(cronJobDTO);
        }
        catch (Exception e){
            cronJobDTO.setStatus(Constants.CRON_JOB_STATUS.FAILED.getKey());
            cronJobDTO.setLogInfo(validateService.responseEntityException(e,499).getMessage());
            cronJobDTO.setEndTime(DateUtils.instantCurrentDateTime());
            cronJobService.update(cronJobDTO);
        }
    }

    @Override
    public void ScheduleBitcoinAnalyzePreviousWeek(CronScheduleDTO cronScheduleDTO) {
        CronJobDTO cronJobDTO = new CronJobDTO(cronScheduleDTO);
        try {
            cronJobDTO = cronJobService.saveAndFlush(cronJobDTO);
            //Becasuse I have not had must time, so I didn't apply insertion to bitcoinAnalyze.cron_job_id for clarify the record belong to which cron_job
            bitcoinAnalyzeService.processAnalyzeByTimeType(Constants.WEEKLY,null);
            cronJobDTO.setEndTime(DateUtils.instantCurrentDateTime());
            cronJobDTO.setStatus(Constants.CRON_JOB_STATUS.COMPLETE.getKey());
            cronJobService.update(cronJobDTO);
        }
        catch (Exception e){
            cronJobDTO.setStatus(Constants.CRON_JOB_STATUS.FAILED.getKey());
            cronJobDTO.setLogInfo(validateService.responseEntityException(e,499).getMessage());
            cronJobDTO.setEndTime(DateUtils.instantCurrentDateTime());
            cronJobService.update(cronJobDTO);
        }
    }

    @Override
    public void ScheduleBitcoinAnalyzePreviousMonth(CronScheduleDTO cronScheduleDTO) {
        CronJobDTO cronJobDTO = new CronJobDTO(cronScheduleDTO);
        try {
            cronJobDTO = cronJobService.saveAndFlush(cronJobDTO);
            //Becasuse I have not had must time, so I didn't apply insertion to bitcoinAnalyze.cron_job_id for clarify the record belong to which cron_job
            bitcoinAnalyzeService.processAnalyzeByTimeType(Constants.MONTHLY,null);
            cronJobDTO.setEndTime(DateUtils.instantCurrentDateTime());
            cronJobDTO.setStatus(Constants.CRON_JOB_STATUS.COMPLETE.getKey());
            cronJobService.update(cronJobDTO);
        }
        catch (Exception e){
            cronJobDTO.setStatus(Constants.CRON_JOB_STATUS.FAILED.getKey());
            cronJobDTO.setLogInfo(validateService.responseEntityException(e,499).getMessage());
            cronJobDTO.setEndTime(DateUtils.instantCurrentDateTime());
            cronJobService.update(cronJobDTO);
        }
    }

    @Override
    public String startSchedule() {
        postProcessor.postProcessAfterInitialization(this, Constants.SCHEDULED_TASKS);
        return "Started";
    }

    @Override
    public String stopSchedule() {
        postProcessor.postProcessBeforeDestruction(this, Constants.SCHEDULED_TASKS);
        return "Stopped";
    }

    @Override
    public String listSchedules() {
        Set<ScheduledTask> setTasks = postProcessor.getScheduledTasks();
        if (!setTasks.isEmpty()) {
            return setTasks.toString();
        } else {
            return "Currently no scheduler tasks are running";
        }
    }

    public static JobDetail buildJobDetail(final Class jobClass, final ScheduleFactory scheduleFactory) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(jobClass.getSimpleName(), scheduleFactory);
        return JobBuilder.newJob(jobClass).withIdentity(scheduleFactory.getClass().getSimpleName()).setJobData(jobDataMap).build();
    }

    public static Trigger buildTrigger(final Class jobClass, final ScheduleFactory scheduleFactory) {
        String cron = scheduleFactory.getTimeSchedule().toString();
        return TriggerBuilder
            .newTrigger()
            .withIdentity(scheduleFactory.getClass().getSimpleName())
            .withSchedule(CronScheduleBuilder.cronSchedule(scheduleFactory.getTimeSchedule()))
            .build();
    }

    public void schedule(final Class jobClass, final ScheduleFactory scheduleFactory) {
        final JobDetail jobDetail = buildJobDetail(jobClass, scheduleFactory);
        final Trigger trigger = buildTrigger(jobClass, scheduleFactory);
        ((CronTriggerImpl) trigger).setTimeZone(TimeZone.getTimeZone(timeZone));
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            validateService.responseEntityException(e,-1);
        }
    }

    @PostConstruct
    public void init() {
        try {
            TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
            List<CronScheduleDTO> cronScheduleDTOs = cronScheduleService.findAllByIsActiveOrderByIdAsc(Constants.YES);
            if(!cronScheduleDTOs.isEmpty()){
                Map<String,CronScheduleDTO>  mapCronScheduleDTOs = new HashMap<>();
                mapCronScheduleDTOs = cronScheduleDTOs.stream().collect(Collectors.toMap(CronScheduleDTO::getJobName, m -> m));
                
                ScheduleFactory scheduleFetchCurrentPrice = new ScheduleFetchCurrentPrice(this);
                scheduleFetchCurrentPrice.setTimeSchedule(mapCronScheduleDTOs.get(ScheduleFetchCurrentPrice.class.getSimpleName()).getCronExpression());
                scheduleFetchCurrentPrice.setCronScheduleDTO(mapCronScheduleDTOs.get(ScheduleFetchCurrentPrice.class.getSimpleName()));
                schedule(JobExecute.class, scheduleFetchCurrentPrice);

                ScheduleFactory scheduleBitcoinAnalyzePreviousDay = new ScheduleBitcoinAnalyzePreviousDay(this);
                scheduleBitcoinAnalyzePreviousDay.setTimeSchedule(mapCronScheduleDTOs.get(ScheduleBitcoinAnalyzePreviousDay.class.getSimpleName()).getCronExpression());
                scheduleBitcoinAnalyzePreviousDay.setCronScheduleDTO(mapCronScheduleDTOs.get(ScheduleBitcoinAnalyzePreviousDay.class.getSimpleName()));
                schedule(JobExecute.class, scheduleBitcoinAnalyzePreviousDay);

                ScheduleFactory scheduleBitcoinAnalyzePreviousWeek = new ScheduleBitcoinAnalyzePreviousWeek(this);
                scheduleBitcoinAnalyzePreviousWeek.setTimeSchedule(mapCronScheduleDTOs.get(ScheduleBitcoinAnalyzePreviousWeek.class.getSimpleName()).getCronExpression());
                scheduleBitcoinAnalyzePreviousWeek.setCronScheduleDTO(mapCronScheduleDTOs.get(ScheduleBitcoinAnalyzePreviousWeek.class.getSimpleName()));
                schedule(JobExecute.class, scheduleBitcoinAnalyzePreviousWeek);

                ScheduleFactory scheduleBitcoinAnalyzePreviousMonth = new ScheduleBitcoinAnalyzePreviousMonth(this);
                scheduleBitcoinAnalyzePreviousMonth.setTimeSchedule(mapCronScheduleDTOs.get(ScheduleBitcoinAnalyzePreviousMonth.class.getSimpleName()).getCronExpression());
                scheduleBitcoinAnalyzePreviousMonth.setCronScheduleDTO(mapCronScheduleDTOs.get(ScheduleBitcoinAnalyzePreviousMonth.class.getSimpleName()));
                schedule(JobExecute.class, scheduleBitcoinAnalyzePreviousMonth);

                scheduler.start();
            }
        } catch (Exception e) {
            validateService.responseEntityException(e,-1);
        }
    }

    @PreDestroy
    public void preDestroy() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            validateService.responseEntityException(e,-1);
        }
    }
}
