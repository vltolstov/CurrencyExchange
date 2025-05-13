package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateRequestDto {

    private String baseCurrencyCode;

    private String targetCurrencyCode;

    private Double rate;

}
