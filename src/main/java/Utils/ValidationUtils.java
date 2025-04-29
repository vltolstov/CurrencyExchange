package Utils;

import exceptions.InvalidParameterException;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtils {

    private static Set<String> currencyCodes;

    public static void validate (){
        //валидация ввода при добавлении новой валюты
    }

    public static void validateCurrencyCode(String code){

        if(code.length() != 3){
            throw new InvalidParameterException("Invalid currency code length");
        }

        if(currencyCodes == null){
            Set<Currency> currencies = Currency.getAvailableCurrencies();

            currencyCodes = currencies.stream()
                    .map(Currency::getCurrencyCode)
                    .collect(Collectors.toSet());
        }

        if(!currencyCodes.contains(code)){
            throw new InvalidParameterException("model.Currency code not valid. Use ISO 4217 format");
        }

    }

}
