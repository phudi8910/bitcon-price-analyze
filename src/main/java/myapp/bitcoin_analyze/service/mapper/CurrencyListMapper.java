package myapp.bitcoin_analyze.service.mapper;

import myapp.bitcoin_analyze.domain.CurrencyList;
import myapp.bitcoin_analyze.service.dto.CurrencyListDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CurrencyList} and its DTO {@link CurrencyListDTO}.
 */
@Mapper(componentModel = "spring")
public interface CurrencyListMapper extends EntityMapper<CurrencyListDTO, CurrencyList> {}
