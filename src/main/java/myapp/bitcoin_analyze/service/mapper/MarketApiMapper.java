package myapp.bitcoin_analyze.service.mapper;

import myapp.bitcoin_analyze.domain.MarketApi;
import myapp.bitcoin_analyze.service.dto.MarketApiDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MarketApi} and its DTO {@link MarketApiDTO}.
 */
@Mapper(componentModel = "spring")
public interface MarketApiMapper extends EntityMapper<MarketApiDTO, MarketApi> {}
