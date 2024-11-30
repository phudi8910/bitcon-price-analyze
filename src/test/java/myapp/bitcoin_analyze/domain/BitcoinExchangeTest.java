package myapp.bitcoin_analyze.domain;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.bitcoin_analyze.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BitcoinExchangeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BitcoinExchange.class);
        BitcoinExchange bitcoinExchange1 = new BitcoinExchange();
        bitcoinExchange1.setId(1L);
        BitcoinExchange bitcoinExchange2 = new BitcoinExchange();
        bitcoinExchange2.setId(bitcoinExchange1.getId());
        assertThat(bitcoinExchange1).isEqualTo(bitcoinExchange2);
        bitcoinExchange2.setId(2L);
        assertThat(bitcoinExchange1).isNotEqualTo(bitcoinExchange2);
        bitcoinExchange1.setId(null);
        assertThat(bitcoinExchange1).isNotEqualTo(bitcoinExchange2);
    }
}
