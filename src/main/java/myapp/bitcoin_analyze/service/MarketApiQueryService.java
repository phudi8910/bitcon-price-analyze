package myapp.bitcoin_analyze.service;

import jakarta.persistence.criteria.Predicate;
import myapp.bitcoin_analyze.domain.MarketApi;
import myapp.bitcoin_analyze.repository.MarketApiRepository;
import myapp.bitcoin_analyze.service.dto.MarketApiDTO;
import myapp.bitcoin_analyze.service.mapper.MarketApiMapper;
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
public class MarketApiQueryService {
    private final Logger log = LoggerFactory.getLogger(MarketApiQueryService.class);

    @Autowired
    private MarketApiRepository marketApiRepository;

    @Autowired
    private MarketApiMapper marketApiMapper;

    @Transactional(readOnly = true)
    public Page<MarketApiDTO> findByCriteria(MarketApiDTO marketApiDTO, Pageable page) {
        log.debug("find by criteria : {}, page: {}", marketApiDTO, page);
        final Specification<MarketApi> specification = createSpecification(marketApiDTO);
        return marketApiRepository.findAll(specification, page).map(marketApiMapper::toDto);
    }

    protected Specification<MarketApi> createSpecification(MarketApiDTO marketApiDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (marketApiDTO.getId() != null) {
                predicates.add(cb.equal(root.get("id"), marketApiDTO.getId()));
            }

            if (marketApiDTO.getMarketName() != null) {
                predicates.add(cb.equal(root.get("marketName"), marketApiDTO.getMarketName()));
            }

            if (marketApiDTO.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), marketApiDTO.getIsActive()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}


