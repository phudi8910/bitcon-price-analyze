package myapp.bitcoin_analyze.service;

import myapp.bitcoin_analyze.service.dto.BitcoinExchangeDTO;
import myapp.bitcoin_analyze.service.dto.CronJobDTO;
import myapp.bitcoin_analyze.service.dto.CurrencyListDTO;
import myapp.bitcoin_analyze.service.dto.MarketApiDTO;
import org.json.JSONObject;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

public abstract class IntegrationFactory {

    public abstract List<BitcoinExchangeDTO> fetchCurrentPrice(MarketApiDTO marketApiDTO, List<CurrencyListDTO> currencyListDTOs, Environment environment,CronJobDTO cronJobDTO)  throws Exception;

    public void currencyBinding(JSONObject currencyObj, BitcoinExchangeDTO bitcoinExchangeDTO, String[] responseFields, String[] responseDatatypes, String[] persists) throws Exception {
        Class<?> mclass = Class.forName(bitcoinExchangeDTO.getClass().getName());
        Method method;
        for(int j=0;j<responseFields.length;j++) {
            switch (responseDatatypes[j]) {
                case "String":
                    if(currencyObj.has(responseFields[j])) {
                        method = mclass.getMethod(persists[j],String.class);
                        method.invoke(bitcoinExchangeDTO,currencyObj.getString(responseFields[j]));
                    }
                    break;
                case "BigDecimal":
                    method = mclass.getMethod(persists[j], BigDecimal.class);
                    method.invoke(bitcoinExchangeDTO,currencyObj.getBigDecimal(responseFields[j]));
                    break;
                case "Integer":
                    method = mclass.getMethod(persists[j],Integer.class);
                    method.invoke(bitcoinExchangeDTO,currencyObj.getInt(responseFields[j]));
                    break;
                default:
                    break;
            }
        }
    }
}
