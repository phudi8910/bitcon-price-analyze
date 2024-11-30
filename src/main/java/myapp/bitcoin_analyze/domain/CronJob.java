package myapp.bitcoin_analyze.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import jakarta.persistence.*;

/**
 * A CronJob.
 */
@Entity
@Table(name = "cron_job")
public class CronJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "status")
    private String status;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "log_info")
    private String logInfo;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JsonIgnoreProperties(value = { "cronJobs" }, allowSetters = true)
    private CronSchedule cronSchedule;

    public Long getId() {
        return this.id;
    }

    public CronJob id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return this.jobName;
    }

    public CronJob jobName(String jobName) {
        this.setJobName(jobName);
        return this;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public CronJob cronExpression(String cronExpression) {
        this.setCronExpression(cronExpression);
        return this;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getStatus() {
        return this.status;
    }

    public CronJob status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public CronJob startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public CronJob endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getLogInfo() {
        return this.logInfo;
    }

    public CronJob logInfo(String logInfo) {
        this.setLogInfo(logInfo);
        return this;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public String getDescription() {
        return this.description;
    }

    public CronJob description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CronSchedule getCronSchedule() {
        return this.cronSchedule;
    }

    public void setCronSchedule(CronSchedule cronSchedule) {
        this.cronSchedule = cronSchedule;
    }

    public CronJob cronSchedule(CronSchedule cronSchedule) {
        this.setCronSchedule(cronSchedule);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CronJob)) {
            return false;
        }
        return id != null && id.equals(((CronJob) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CronJob{" +
            "id=" + getId() +
            ", jobName='" + getJobName() + "'" +
            ", cronExpression='" + getCronExpression() + "'" +
            ", status='" + getStatus() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", logInfo='" + getLogInfo() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
