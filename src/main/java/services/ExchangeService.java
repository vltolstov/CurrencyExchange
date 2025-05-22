package services;

import dao.ExchangeRateDao;
import dao.JdbcExchangeRateDao;
import dto.ExchangeRequestDto;
import dto.ExchangeResponseDto;
import exceptions.NotFoundException;
import models.ExchangeRate;
import static Utils.MappingUtils.convertToDto;

import java.util.Optional;

public class ExchangeService {

    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();

    public ExchangeResponseDto exchange(ExchangeRequestDto exchangeRequestDto) {

        ExchangeRate exchangeRate = findExchangeRate(exchangeRequestDto)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Exchange not found for '$s' and '$s' currency codes",
                        exchangeRequestDto.getFromCurrencyCode(),
                        exchangeRequestDto.getToCurrencyCode()
                        )
                ));

        Double amount = exchangeRequestDto.getAmount();
        Double convertedAmount = Math.round(amount * exchangeRate.getRate() * 100) / 100.0;

        return new ExchangeResponseDto(
            convertToDto(exchangeRate.getBaseCurrency()),
            convertToDto(exchangeRate.getTargetCurrency()),
            exchangeRate.getRate(),
            amount,
            convertedAmount
        );
    }

    private Optional<ExchangeRate> findExchangeRate(ExchangeRequestDto exchangeRequestDto){
        String fromCurrencyCode = exchangeRequestDto.getFromCurrencyCode();
        String toCurrencyCode = exchangeRequestDto.getToCurrencyCode();

        Optional<ExchangeRate> exchangeRate = findDirectExchange(fromCurrencyCode, toCurrencyCode);

        if(exchangeRate.isEmpty()){
            exchangeRate = findInDirectExchange(fromCurrencyCode, toCurrencyCode);
        }

        if(exchangeRate.isEmpty()){
            exchangeRate = findCrossExchange(fromCurrencyCode, toCurrencyCode);
        }

        return exchangeRate;
    }

    private Optional<ExchangeRate> findDirectExchange(String fromCurrencyCode, String toCurrencyCode) {
        return exchangeRateDao.findByCodes(fromCurrencyCode, toCurrencyCode);
    }

    private Optional<ExchangeRate> findInDirectExchange(String fromCurrencyCode, String toCurrencyCode) {
        Optional<ExchangeRate> optionalExchangeRate = exchangeRateDao.findByCodes(toCurrencyCode, fromCurrencyCode);

        if(optionalExchangeRate.isEmpty()){
            return Optional.empty();
        }

        ExchangeRate indirectExchangeRate = optionalExchangeRate.get();
        Double rate = Math.round(1 / indirectExchangeRate.getRate() * 100) / 100.0;

        ExchangeRate exchangeRate = new ExchangeRate(indirectExchangeRate.getTargetCurrency(), indirectExchangeRate.getBaseCurrency(), rate);

        return Optional.of(exchangeRate);
    }

    private Optional<ExchangeRate> findCrossExchange(String fromCurrencyCode, String toCurrencyCode) {
        return Optional.of(new ExchangeRate());
    }
}
