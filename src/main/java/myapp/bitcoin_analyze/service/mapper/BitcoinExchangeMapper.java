package myapp.bitcoin_analyze.service.mapper;

import myapp.bitcoin_analyze.domain.BitcoinExchange;
import myapp.bitcoin_analyze.domain.MarketApi;
import myapp.bitcoin_analyze.service.dto.BitcoinExchangeDTO;
import myapp.bitcoin_analyze.service.dto.MarketApiDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BitcoinExchange} and its DTO {@link BitcoinExchangeDTO}.
 */
@Mapper(componentModel = "spring")
public interface BitcoinExchangeMapper extends EntityMapper<BitcoinExchangeDTO, BitcoinExchange> {
    @Mapping(target = "marketApi", source = "marketApi", qualifiedByName = "marketApiId")
    BitcoinExchangeDTO toDto(BitcoinExchange s);

//    @Mapping(ignore = true, target = "marketApi")
//    BitcoinExchangeDTO bitcoinExchangeToBitcoinExchangeDTO(BitcoinExchange bitcoinExchange);

    @Named("marketApiId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MarketApiDTO toDtoMarketApiId(MarketApi marketApi);
}
