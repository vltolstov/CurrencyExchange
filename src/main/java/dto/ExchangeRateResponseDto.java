package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateResponseDto {

    private Integer id;

    private CurrencyResponseDto baseCurrency;

    private CurrencyResponseDto targetCurrency;

    private Double rate;
}
