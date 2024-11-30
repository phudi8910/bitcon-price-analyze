package myapp.bitcoin_analyze.service.mapper;

import myapp.bitcoin_analyze.domain.BitcoinAnalyze;
import myapp.bitcoin_analyze.service.dto.BitcoinAnalyzeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BitcoinAnalyze} and its DTO {@link BitcoinAnalyzeDTO}.
 */
@Mapper(componentModel = "spring")
public interface BitcoinAnalyzeMapper extends EntityMapper<BitcoinAnalyzeDTO, BitcoinAnalyze> {}
