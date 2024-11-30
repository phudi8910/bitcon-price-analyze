package myapp.bitcoin_analyze.repository;

import myapp.bitcoin_analyze.domain.BitcoinAnalyze;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Spring Data SQL repository for the BitcoinAnalyze entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BitcoinAnalyzeRepository extends JpaRepository<BitcoinAnalyze, Long>, JpaSpecificationExecutor<BitcoinAnalyze> {
    @Transactional
    @Modifying
    Long deleteByCreatedDateBetweenAndTimeType(LocalDate createdDateFrom,LocalDate createdDateTo,String timeType);


    @Transactional
    @Modifying
    Long deleteByCreatedDateBetweenAndTimeTypeAndCurrencyCode(LocalDate createdDateFrom,LocalDate createdDateTo,String timeType,String currencyCode);

    @Transactional
    @Query(value = "select (-1*ord) - case :timeType when 'DAILY' then 0 when 'WEEKLY' then 3 when 'MONTHLY' then 6 end id,a1.market_api_id,currency_code,symbol,:toDate created_date,current_timestamp created_time_stamp,\n" +
            "max(max_rate_float) max_rate_float,\n" +
            "max(min_rate_float) min_rate_float,\n" +
            "max(ratio_rate_float) ratio_rate_float,\n" +
            "max(open_rate_float) open_rate_float,\n" +
            "max(closed_rate_float)  closed_rate_float,\n" +
            "round((max(closed_rate_float) - max(open_rate_float)) *100/ max(open_rate_float)) ratio_open_closed_rate,\n" +
            ":timeType time_type,null description\n" +
            "from (\n" +
            "\tselect \n" +
            "\tmarket_api_id,currency_code,symbol,created_date,\n" +
            "\tmax_rate_float,min_rate_float,max_rate_float-min_rate_float ratio_rate_float,\n" +
            "\tcase when max_created_time_stamp=created_time_stamp then rate_float else 0 end closed_rate_float,\n" +
            "\tcase when min_created_time_stamp=created_time_stamp then rate_float else 0 end open_rate_float\n" +
            "\tfrom (\n" +
            "\tselect \n" +
            "\tmax(rate_float) over (partition by market_api_id,currency_code) max_rate_float ,\n" +
            "\tmin(rate_float) over (partition by market_api_id,currency_code) min_rate_float ,\n" +
            "\tmax(created_time_stamp) over (partition by market_api_id,currency_code) max_created_time_stamp ,\n" +
            "\tmin(created_time_stamp) over (partition by market_api_id,currency_code) min_created_time_stamp, *\n" +
            "\tfrom bitcoin_exchange where created_date >= :fromDate and created_date<=:toDate and currency_code=case when :currencyCode is null then currency_code else :currencyCode end \n" +
            "\t) a\n" +
            ") a1 inner join currency_list b1 on a1.currency_code=b1.id and a1.market_api_id=b1.market_api_id \n" +
            "group by a1.market_api_id,currency_code,symbol,ord", nativeQuery = true)
    List<BitcoinAnalyze> preparationAnalyzeByTimeType(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("timeType") String timeType,
            @Param("currencyCode") String currencyCode);
}
