package dao;

import models.Currency;

import java.util.Optional;

public interface CurrencyDao extends CrudDao<Currency, Integer> {

    Optional<Currency> findByCode(String code);

}
