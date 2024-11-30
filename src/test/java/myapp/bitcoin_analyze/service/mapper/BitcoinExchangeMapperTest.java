package myapp.bitcoin_analyze.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BitcoinExchangeMapperTest {

    private BitcoinExchangeMapper bitcoinExchangeMapper;

    @BeforeEach
    public void setUp() {
        bitcoinExchangeMapper = new BitcoinExchangeMapperImpl();
    }
}
