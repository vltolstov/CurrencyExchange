package Utils;

import dto.CurrencyRequestDto;
import dto.CurrencyResponseDto;
import dto.ExchangeRateResponseDto;
import models.Currency;
import models.ExchangeRate;
import org.modelmapper.ModelMapper;

public class MappingUtils {

    private static final ModelMapper MODEL_MAPPER;

    static {
        MODEL_MAPPER = new ModelMapper();

        MODEL_MAPPER.typeMap(CurrencyRequestDto.class, Currency.class)
                .addMapping(CurrencyRequestDto::getName, Currency::setFullName);
    }

    public static Currency convertToEntity(CurrencyRequestDto currencyRequestDto){
        return MODEL_MAPPER.map(currencyRequestDto, Currency.class);
    }

    public static CurrencyResponseDto convertToDto(Currency currency){
        return MODEL_MAPPER.map(currency, CurrencyResponseDto.class);
    }

    public static ExchangeRateResponseDto convertToDto(ExchangeRate exchangeRate){
        return MODEL_MAPPER.map(exchangeRate, ExchangeRateResponseDto.class);
    }

}
