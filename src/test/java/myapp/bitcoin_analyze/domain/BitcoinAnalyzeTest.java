package myapp.bitcoin_analyze.domain;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.bitcoin_analyze.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BitcoinAnalyzeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BitcoinAnalyze.class);
        BitcoinAnalyze bitcoinAnalyze1 = new BitcoinAnalyze();
        bitcoinAnalyze1.setId(1L);
        BitcoinAnalyze bitcoinAnalyze2 = new BitcoinAnalyze();
        bitcoinAnalyze2.setId(bitcoinAnalyze1.getId());
        assertThat(bitcoinAnalyze1).isEqualTo(bitcoinAnalyze2);
        bitcoinAnalyze2.setId(2L);
        assertThat(bitcoinAnalyze1).isNotEqualTo(bitcoinAnalyze2);
        bitcoinAnalyze1.setId(null);
        assertThat(bitcoinAnalyze1).isNotEqualTo(bitcoinAnalyze2);
    }
}
