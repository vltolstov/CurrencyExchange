package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeResponseDto {

    private CurrencyResponseDto baseCurrency;

    private CurrencyResponseDto targetCurrency;

    private Double rate;

    private Double amount;

    private Double convertedAmount;

}
