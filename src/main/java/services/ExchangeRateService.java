package services;

import dao.CurrencyDao;
import dao.ExchangeRateDao;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.ExchangeRateRequestDto;
import exceptions.NotFoundException;
import models.Currency;
import models.ExchangeRate;

public class ExchangeRateService {

    private static final CurrencyDao currencyDao = new JdbcCurrencyDao();
    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();

    public ExchangeRate save(ExchangeRateRequestDto exchangeRateRequestDto){
        ExchangeRate exchangeRate = initExchangeRate(exchangeRateRequestDto);
        return exchangeRateDao.save(exchangeRate);
    }

    public ExchangeRate update(ExchangeRateRequestDto exchangeRateRequestDto){
        ExchangeRate exchangeRate = initExchangeRate(exchangeRateRequestDto);
        return exchangeRateDao.update(exchangeRate)
                .orElseThrow(() -> new NotFoundException("Failed to update exchange rate with codes " + exchangeRateRequestDto.getBaseCurrencyCode() + " and " + exchangeRateRequestDto.getTargetCurrencyCode()));
    }

    private static ExchangeRate initExchangeRate(ExchangeRateRequestDto exchangeRateRequestDto){

        String baseCurrencyCode = exchangeRateRequestDto.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRateRequestDto.getTargetCurrencyCode();

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new NotFoundException("Currency with code " + baseCurrencyCode + " not found"));
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(() -> new NotFoundException("Currency with code " + targetCurrencyCode + " not found"));

        return new ExchangeRate(baseCurrency, targetCurrency, exchangeRateRequestDto.getRate());
    }

}
