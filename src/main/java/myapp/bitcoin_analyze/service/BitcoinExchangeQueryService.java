package myapp.bitcoin_analyze.service;

import jakarta.persistence.criteria.Predicate;
import myapp.bitcoin_analyze.domain.BitcoinExchange;
import myapp.bitcoin_analyze.repository.BitcoinExchangeRepository;
import myapp.bitcoin_analyze.repository.BitcoinExchangeRepository;
import myapp.bitcoin_analyze.service.dto.BitcoinExchangeDTO;
import myapp.bitcoin_analyze.service.dto.RequestDTO;
import myapp.bitcoin_analyze.service.mapper.BitcoinExchangeMapper;
import myapp.bitcoin_analyze.service.mapper.BitcoinExchangeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
public class BitcoinExchangeQueryService {
    private final Logger log = LoggerFactory.getLogger(BitcoinExchangeQueryService.class);

    @Autowired
    private BitcoinExchangeRepository bitcoinExchangeRepository;

    @Autowired
    private BitcoinExchangeMapper bitcoinExchangeMapper;

    @Transactional(readOnly = true)
    public Page<BitcoinExchangeDTO> findByCriteria(RequestDTO requestDTO, Pageable page) {
        log.debug("find by criteria : {}, page: {}", requestDTO, page);
        final Specification<BitcoinExchange> specification = createSpecification(requestDTO);
        return bitcoinExchangeRepository.findAll(specification, page).map(bitcoinExchangeMapper::toDto);
    }

    protected Specification<BitcoinExchange> createSpecification(RequestDTO requestDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (requestDTO.getCurrencyCode() != null) {
                predicates.add(cb.equal(root.get("currencyCode"), requestDTO.getCurrencyCode()));
            }

            if (requestDTO.getMarketId() != null) {
                predicates.add(cb.equal(root.get("marketApi").get("id"), requestDTO.getMarketId()));
            }

            if (requestDTO.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), requestDTO.getFromDate()));
            }

            if (requestDTO.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdDate"), requestDTO.getToDate()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}


