package servlets;

import Utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRateDao;
import dao.JdbcExchangeRateDao;
import dto.ExchangeRateRequestDto;
import exceptions.InvalidParameterException;
import exceptions.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ExchangeRate;
import services.ExchangeRateService;

import java.io.IOException;

import static Utils.MappingUtils.convertToDto;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getMethod().equals("PATCH")){
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String codes = request.getPathInfo().replaceFirst("/", "");

        if(codes.length() != 6) {
            throw new InvalidParameterException("Codes of exchange rate is not valid");
        }

        String baseCurrencyCode = codes.substring(0, 3);
        String targetCurrencyCode = codes.substring(3);

        ValidationUtils.validateCurrencyCode(baseCurrencyCode);
        ValidationUtils.validateCurrencyCode(targetCurrencyCode);

        ExchangeRate exchangeRate = exchangeRateDao.findByCodes(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new NotFoundException("Exchange rate with codes " + baseCurrencyCode + " and " + targetCurrencyCode + " not found"));

        objectMapper.writeValue(response.getWriter(), convertToDto(exchangeRate));
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String codes = request.getPathInfo().replaceFirst("/", "");

        if(codes.length() != 6) {
            throw new InvalidParameterException("Codes of exchange rate is not valid");
        }

        String baseCurrencyCode = codes.substring(0, 3);
        String targetCurrencyCode = codes.substring(3);

        ValidationUtils.validateCurrencyCode(baseCurrencyCode);
        ValidationUtils.validateCurrencyCode(targetCurrencyCode);

        String parameter = request.getReader().readLine();
        if(parameter == null) {
            throw new InvalidParameterException("Parameters is empty");
        }

        String rate = parameter.replace("rate=", "");
        if(rate.isBlank()) {
            throw new InvalidParameterException("Rate is empty");
        }

        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, convertRateToDouble(rate));
        ExchangeRate exchangeRate = exchangeRateService.update(exchangeRateRequestDto);

        objectMapper.writeValue(response.getWriter(), convertToDto(exchangeRate));
    }

    private Double convertRateToDouble(String rate){
        try {
            return Double.valueOf(rate);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Rate is not valid");
        }
    }

}
