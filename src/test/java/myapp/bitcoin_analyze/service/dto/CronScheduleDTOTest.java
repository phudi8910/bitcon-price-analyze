package myapp.bitcoin_analyze.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.bitcoin_analyze.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CronScheduleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CronScheduleDTO.class);
        CronScheduleDTO cronScheduleDTO1 = new CronScheduleDTO();
        cronScheduleDTO1.setId(1L);
        CronScheduleDTO cronScheduleDTO2 = new CronScheduleDTO();
        assertThat(cronScheduleDTO1).isNotEqualTo(cronScheduleDTO2);
        cronScheduleDTO2.setId(cronScheduleDTO1.getId());
        assertThat(cronScheduleDTO1).isEqualTo(cronScheduleDTO2);
        cronScheduleDTO2.setId(2L);
        assertThat(cronScheduleDTO1).isNotEqualTo(cronScheduleDTO2);
        cronScheduleDTO1.setId(null);
        assertThat(cronScheduleDTO1).isNotEqualTo(cronScheduleDTO2);
    }
}
