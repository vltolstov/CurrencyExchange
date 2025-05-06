package servlets;

import Utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRateDao;
import dao.JdbcExchangeRateDao;
import exceptions.InvalidParameterException;
import exceptions.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ExchangeRate;

import java.io.IOException;

import static Utils.MappingUtils.convertToDto;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String codes = request.getPathInfo().replaceFirst("/", "");

        if(codes.length() != 6) {
            throw new InvalidParameterException("Codes of exchange rate is not valid");
        }

        String baseCurrencyCode = codes.substring(0, 3);
        String targetCurrencyCode = codes.substring(3, codes.length());

        ValidationUtils.validateCurrencyCode(baseCurrencyCode);
        ValidationUtils.validateCurrencyCode(targetCurrencyCode);

        ExchangeRate exchangeRate = exchangeRateDao.findByCodes(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new NotFoundException("Exchange rate with codes " + baseCurrencyCode + " and " + targetCurrencyCode + " not found"));

        objectMapper.writeValue(response.getWriter(), convertToDto(exchangeRate));
    }
}
