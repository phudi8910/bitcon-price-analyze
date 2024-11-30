package myapp.bitcoin_analyze.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.bitcoin_analyze.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CronJobDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CronJobDTO.class);
        CronJobDTO cronJobDTO1 = new CronJobDTO();
        cronJobDTO1.setId(1L);
        CronJobDTO cronJobDTO2 = new CronJobDTO();
        assertThat(cronJobDTO1).isNotEqualTo(cronJobDTO2);
        cronJobDTO2.setId(cronJobDTO1.getId());
        assertThat(cronJobDTO1).isEqualTo(cronJobDTO2);
        cronJobDTO2.setId(2L);
        assertThat(cronJobDTO1).isNotEqualTo(cronJobDTO2);
        cronJobDTO1.setId(null);
        assertThat(cronJobDTO1).isNotEqualTo(cronJobDTO2);
    }
}
