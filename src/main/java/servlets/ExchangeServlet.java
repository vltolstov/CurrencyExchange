package servlets;

import Utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateRequestDto;
import dto.ExchangeRequestDto;
import dto.ExchangeResponseDto;
import exceptions.InvalidParameterException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.ExchangeService;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExchangeService exchangeService = new ExchangeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String fromCurrencyCode = request.getParameter("from");
        String toCurrencyCode = request.getParameter("to");
        Double amount = Double.parseDouble(request.getParameter("amount"));

        if(fromCurrencyCode == null || toCurrencyCode == null || amount == null) {
            throw new InvalidParameterException("Invalid request parameters");
        }

        ExchangeRequestDto exchangeRequestDto = new ExchangeRequestDto(fromCurrencyCode, toCurrencyCode, amount);

        ValidationUtils.validate(exchangeRequestDto);

        ExchangeResponseDto exchangeResponseDto = exchangeService.exchange(exchangeRequestDto);

        objectMapper.writeValue(response.getWriter(), exchangeResponseDto);
    }
}
