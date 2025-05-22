package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRequestDto {

    private String fromCurrencyCode;

    private String toCurrencyCode;

    private Double amount;

}
