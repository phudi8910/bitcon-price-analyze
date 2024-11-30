package myapp.bitcoin_analyze.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link myapp.bitcoin_analyze.domain.CronSchedule} entity.
 */
public class CronScheduleDTO implements Serializable {

    private Long id;

    private String jobName;

    private String cronExpression;

    private String isActive;

    private String description;

    private Integer ord;

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

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrd() {
        return ord;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CronScheduleDTO)) {
            return false;
        }

        CronScheduleDTO cronScheduleDTO = (CronScheduleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cronScheduleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CronScheduleDTO{" +
            "id=" + getId() +
            ", jobName='" + getJobName() + "'" +
            ", cronExpression='" + getCronExpression() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", description='" + getDescription() + "'" +
            ", ord=" + getOrd() +
            "}";
    }
}
