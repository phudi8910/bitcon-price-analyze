package myapp.bitcoin_analyze.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.bitcoin_analyze.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CurrencyListDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CurrencyListDTO.class);
        CurrencyListDTO currencyListDTO1 = new CurrencyListDTO();
        currencyListDTO1.setId("id1");
        CurrencyListDTO currencyListDTO2 = new CurrencyListDTO();
        assertThat(currencyListDTO1).isNotEqualTo(currencyListDTO2);
        currencyListDTO2.setId(currencyListDTO1.getId());
        assertThat(currencyListDTO1).isEqualTo(currencyListDTO2);
        currencyListDTO2.setId("id2");
        assertThat(currencyListDTO1).isNotEqualTo(currencyListDTO2);
        currencyListDTO1.setId(null);
        assertThat(currencyListDTO1).isNotEqualTo(currencyListDTO2);
    }
}
