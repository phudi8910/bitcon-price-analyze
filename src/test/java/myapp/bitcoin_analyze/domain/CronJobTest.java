package myapp.bitcoin_analyze.domain;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.bitcoin_analyze.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CronJobTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CronJob.class);
        CronJob cronJob1 = new CronJob();
        cronJob1.setId(1L);
        CronJob cronJob2 = new CronJob();
        cronJob2.setId(cronJob1.getId());
        assertThat(cronJob1).isEqualTo(cronJob2);
        cronJob2.setId(2L);
        assertThat(cronJob1).isNotEqualTo(cronJob2);
        cronJob1.setId(null);
        assertThat(cronJob1).isNotEqualTo(cronJob2);
    }
}
