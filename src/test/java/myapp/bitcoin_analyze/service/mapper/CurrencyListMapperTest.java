package myapp.bitcoin_analyze.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CurrencyListMapperTest {

    private CurrencyListMapper currencyListMapper;

    @BeforeEach
    public void setUp() {
        currencyListMapper = new CurrencyListMapperImpl();
    }
}
