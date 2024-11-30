package myapp.bitcoin_analyze.domain;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.bitcoin_analyze.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CurrencyListTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CurrencyList.class);
        CurrencyList currencyList1 = new CurrencyList();
        currencyList1.setId("id1");
        CurrencyList currencyList2 = new CurrencyList();
        currencyList2.setId(currencyList1.getId());
        assertThat(currencyList1).isEqualTo(currencyList2);
        currencyList2.setId("id2");
        assertThat(currencyList1).isNotEqualTo(currencyList2);
        currencyList1.setId(null);
        assertThat(currencyList1).isNotEqualTo(currencyList2);
    }
}
