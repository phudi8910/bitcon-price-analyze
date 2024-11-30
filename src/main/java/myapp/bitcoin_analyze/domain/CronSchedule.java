package myapp.bitcoin_analyze.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

/**
 * A CronSchedule.
 */
@Entity
@Table(name = "cron_schedule")
public class CronSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "is_active")
    private String isActive;

    @Column(name = "description")
    private String description;

    @Column(name = "ord")
    private Integer ord;

    @OneToMany(mappedBy = "cronSchedule")
    @JsonIgnoreProperties(value = { "cronSchedule" }, allowSetters = true)
    private Set<CronJob> cronJobs = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public CronSchedule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return this.jobName;
    }

    public CronSchedule jobName(String jobName) {
        this.setJobName(jobName);
        return this;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public CronSchedule cronExpression(String cronExpression) {
        this.setCronExpression(cronExpression);
        return this;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getIsActive() {
        return this.isActive;
    }

    public CronSchedule isActive(String isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getDescription() {
        return this.description;
    }

    public CronSchedule description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrd() {
        return this.ord;
    }

    public CronSchedule ord(Integer ord) {
        this.setOrd(ord);
        return this;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }

    public Set<CronJob> getCronJobs() {
        return this.cronJobs;
    }

    public void setCronJobs(Set<CronJob> cronJobs) {
        if (this.cronJobs != null) {
            this.cronJobs.forEach(i -> i.setCronSchedule(null));
        }
        if (cronJobs != null) {
            cronJobs.forEach(i -> i.setCronSchedule(this));
        }
        this.cronJobs = cronJobs;
    }

    public CronSchedule cronJobs(Set<CronJob> cronJobs) {
        this.setCronJobs(cronJobs);
        return this;
    }

    public CronSchedule addCronJob(CronJob cronJob) {
        this.cronJobs.add(cronJob);
        cronJob.setCronSchedule(this);
        return this;
    }

    public CronSchedule removeCronJob(CronJob cronJob) {
        this.cronJobs.remove(cronJob);
        cronJob.setCronSchedule(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CronSchedule)) {
            return false;
        }
        return id != null && id.equals(((CronSchedule) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CronSchedule{" +
            "id=" + getId() +
            ", jobName='" + getJobName() + "'" +
            ", cronExpression='" + getCronExpression() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", description='" + getDescription() + "'" +
            ", ord=" + getOrd() +
            "}";
    }
}
