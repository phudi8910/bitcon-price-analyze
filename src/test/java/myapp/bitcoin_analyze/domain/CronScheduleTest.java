package myapp.bitcoin_analyze.domain;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.bitcoin_analyze.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CronScheduleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CronSchedule.class);
        CronSchedule cronSchedule1 = new CronSchedule();
        cronSchedule1.setId(1L);
        CronSchedule cronSchedule2 = new CronSchedule();
        cronSchedule2.setId(cronSchedule1.getId());
        assertThat(cronSchedule1).isEqualTo(cronSchedule2);
        cronSchedule2.setId(2L);
        assertThat(cronSchedule1).isNotEqualTo(cronSchedule2);
        cronSchedule1.setId(null);
        assertThat(cronSchedule1).isNotEqualTo(cronSchedule2);
    }
}
