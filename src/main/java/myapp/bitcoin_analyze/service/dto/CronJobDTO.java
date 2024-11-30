package myapp.bitcoin_analyze.service.dto;

import myapp.bitcoin_analyze.util.Constants;
import myapp.bitcoin_analyze.util.DateUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link myapp.bitcoin_analyze.domain.CronJob} entity.
 */
public class CronJobDTO implements Serializable {

    private Long id;

    private String jobName;

    private String cronExpression;

    private String status;

    private Instant startTime;

    private Instant endTime;

    private String logInfo;

    private String description;

    private CronScheduleDTO cronSchedule;

    public CronJobDTO(){

    }

    public CronJobDTO(CronScheduleDTO cronScheduleDTO){
        this.cronExpression = cronScheduleDTO.getCronExpression();
        this.jobName = cronScheduleDTO.getJobName();
        this.cronSchedule=cronScheduleDTO;
        this.startTime = DateUtils.instantCurrentDateTime();
        this.endTime = this.startTime;
        this.status = Constants.CRON_JOB_STATUS.RUNNING.getKey();
        this.description = cronScheduleDTO.getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CronScheduleDTO getCronSchedule() {
        return cronSchedule;
    }

    public void setCronSchedule(CronScheduleDTO cronSchedule) {
        this.cronSchedule = cronSchedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CronJobDTO)) {
            return false;
        }

        CronJobDTO cronJobDTO = (CronJobDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cronJobDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CronJobDTO{" +
            "id=" + getId() +
            ", jobName='" + getJobName() + "'" +
            ", cronExpression='" + getCronExpression() + "'" +
            ", status='" + getStatus() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", logInfo='" + getLogInfo() + "'" +
            ", description='" + getDescription() + "'" +
            ", cronSchedule=" + getCronSchedule() +
            "}";
    }
}
