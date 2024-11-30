package myapp.bitcoin_analyze.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CronJobMapperTest {

    private CronJobMapper cronJobMapper;

    @BeforeEach
    public void setUp() {
        cronJobMapper = new CronJobMapperImpl();
    }
}
