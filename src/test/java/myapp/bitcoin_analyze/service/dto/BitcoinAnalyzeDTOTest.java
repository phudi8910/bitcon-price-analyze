package myapp.bitcoin_analyze.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.bitcoin_analyze.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BitcoinAnalyzeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BitcoinAnalyzeDTO.class);
        BitcoinAnalyzeDTO bitcoinAnalyzeDTO1 = new BitcoinAnalyzeDTO();
        bitcoinAnalyzeDTO1.setId(1L);
        BitcoinAnalyzeDTO bitcoinAnalyzeDTO2 = new BitcoinAnalyzeDTO();
        assertThat(bitcoinAnalyzeDTO1).isNotEqualTo(bitcoinAnalyzeDTO2);
        bitcoinAnalyzeDTO2.setId(bitcoinAnalyzeDTO1.getId());
        assertThat(bitcoinAnalyzeDTO1).isEqualTo(bitcoinAnalyzeDTO2);
        bitcoinAnalyzeDTO2.setId(2L);
        assertThat(bitcoinAnalyzeDTO1).isNotEqualTo(bitcoinAnalyzeDTO2);
        bitcoinAnalyzeDTO1.setId(null);
        assertThat(bitcoinAnalyzeDTO1).isNotEqualTo(bitcoinAnalyzeDTO2);
    }
}
