package dao;

import models.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDao extends CrudDao<ExchangeRate, Integer>{

    Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode);

}
