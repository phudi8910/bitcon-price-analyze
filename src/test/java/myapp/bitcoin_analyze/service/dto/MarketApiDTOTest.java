package myapp.bitcoin_analyze.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.bitcoin_analyze.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MarketApiDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        MarketApiDTO marketApiDTO1 = MarketApiDTO.builder().build();
        marketApiDTO1.setId("id1");
        MarketApiDTO marketApiDTO2 = MarketApiDTO.builder().build();
        assertThat(marketApiDTO1).isNotEqualTo(marketApiDTO2);
        marketApiDTO2.setId(marketApiDTO1.getId());
        assertThat(marketApiDTO1).isEqualTo(marketApiDTO2);
        marketApiDTO2.setId("id2");
        assertThat(marketApiDTO1).isNotEqualTo(marketApiDTO2);
        marketApiDTO1.setId(null);
        assertThat(marketApiDTO1).isNotEqualTo(marketApiDTO2);
    }
}
