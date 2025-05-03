package Utils;

import dto.CurrencyRequestDto;
import exceptions.InvalidParameterException;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtils {

    private static Set<String> currencyCodes;

    public static void validate (CurrencyRequestDto currencyRequestDto){
        String name = currencyRequestDto.getName();
        String code = currencyRequestDto.getCode();
        String sign = currencyRequestDto.getSign();

        if(name == null || name.isBlank()){
            throw new InvalidParameterException("Invalid currency name");
        }

        if(code == null || code.isBlank()){
            throw new InvalidParameterException("Invalid currency code");
        }

        if(sign == null || sign.isBlank()){
            throw new InvalidParameterException("Invalid currency sign");
        }

        validateCurrencyCode(code);
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
            throw new InvalidParameterException("Currency code not valid. Use ISO 4217 format");
        }

    }

}
