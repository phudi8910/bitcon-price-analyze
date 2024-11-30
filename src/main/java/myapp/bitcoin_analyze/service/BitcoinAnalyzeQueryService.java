package myapp.bitcoin_analyze.service;
import jakarta.persistence.TypedQuery;
import myapp.bitcoin_analyze.domain.BitcoinAnalyze;
import myapp.bitcoin_analyze.repository.BitcoinAnalyzeRepository;
import myapp.bitcoin_analyze.service.dto.BitcoinAnalyzeDTO;
import myapp.bitcoin_analyze.service.dto.RequestDTO;
import myapp.bitcoin_analyze.service.mapper.BitcoinAnalyzeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

@Service
@Transactional(readOnly = true)
public class BitcoinAnalyzeQueryService{
    private final Logger log = LoggerFactory.getLogger(BitcoinAnalyzeQueryService.class);

    @Autowired
    private BitcoinAnalyzeRepository bitcoinAnalyzeRepository;

    @Autowired
    private BitcoinAnalyzeMapper bitcoinAnalyzeMapper;

    @Transactional(readOnly = true)
    public Page<BitcoinAnalyzeDTO> findByCriteria(RequestDTO requestDTO, Pageable page) {
        log.debug("find by criteria : {}, page: {}", requestDTO, page);
        final Specification<BitcoinAnalyze> specification = createSpecification(requestDTO);
        return bitcoinAnalyzeRepository.findAll(specification, page).map(bitcoinAnalyzeMapper::toDto);
    }


//    public List<BitcoinAnalyze> findAllBy(RequestCriteria criteria) {
//        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
//        CriteriaQuery<BitcoinAnalyze> criteriaQuery =
//                criteriaBuilder.createQuery(BitcoinAnalyze.class);
//        Root<BitcoinAnalyze> root = criteriaQuery.from(BitcoinAnalyze.class);
//
//        Predicate[] predicates = new Predicate[1];
//        //predicates[0] = criteriaBuilder.isNull(root.get("itemDescription"));
//        predicates[0] = criteriaBuilder.equal(root.get("timeType"), "MONTHLY");
//        criteriaQuery.select(root).where(predicates);
//
//        TypedQuery<BitcoinAnalyze> query = em.createQuery(criteriaQuery);
//        List<BitcoinAnalyze> bitcoinAnalyze = query.getResultList();
//    return  bitcoinAnalyze;
//    }

    protected Specification<BitcoinAnalyze> createSpecification(RequestDTO requestDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (requestDTO.getCurrencyCode() != null) {
                predicates.add(cb.equal(root.get("currencyCode"), requestDTO.getCurrencyCode()));
            }

            if (requestDTO.getTimeType() != null) {
                predicates.add(cb.equal(root.get("timeType"), requestDTO.getTimeType()));
            }

            if (requestDTO.getMarketId() != null) {
                predicates.add(cb.equal(root.get("marketApiId"), requestDTO.getMarketId()));
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


