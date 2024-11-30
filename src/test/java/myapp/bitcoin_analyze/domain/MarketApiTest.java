package myapp.bitcoin_analyze.domain;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.bitcoin_analyze.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MarketApiTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MarketApi.class);
        MarketApi marketApi1 = new MarketApi();
        marketApi1.setId("id1");
        MarketApi marketApi2 = new MarketApi();
        marketApi2.setId(marketApi1.getId());
        assertThat(marketApi1).isEqualTo(marketApi2);
        marketApi2.setId("id2");
        assertThat(marketApi1).isNotEqualTo(marketApi2);
        marketApi1.setId(null);
        assertThat(marketApi1).isNotEqualTo(marketApi2);
    }
}
