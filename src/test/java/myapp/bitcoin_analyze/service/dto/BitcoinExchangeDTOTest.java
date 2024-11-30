package myapp.bitcoin_analyze.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.bitcoin_analyze.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BitcoinExchangeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        BitcoinExchangeDTO bitcoinExchangeDTO1 = BitcoinExchangeDTO.builder().build();
        bitcoinExchangeDTO1.setId(1L);
        BitcoinExchangeDTO bitcoinExchangeDTO2 = BitcoinExchangeDTO.builder().build();
        assertThat(bitcoinExchangeDTO1).isNotEqualTo(bitcoinExchangeDTO2);
        bitcoinExchangeDTO2.setId(bitcoinExchangeDTO1.getId());
        assertThat(bitcoinExchangeDTO1).isEqualTo(bitcoinExchangeDTO2);
        bitcoinExchangeDTO2.setId(2L);
        assertThat(bitcoinExchangeDTO1).isNotEqualTo(bitcoinExchangeDTO2);
        bitcoinExchangeDTO1.setId(null);
        assertThat(bitcoinExchangeDTO1).isNotEqualTo(bitcoinExchangeDTO2);
    }
}
