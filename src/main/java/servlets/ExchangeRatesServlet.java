package servlets;

import Utils.MappingUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRateDao;
import dao.JdbcExchangeRateDao;
import dto.ExchangeRateResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ExchangeRate;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //получить все ExchangeRates
        List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();

        List<ExchangeRateResponseDto> exchangeRatesDto = exchangeRates.stream()
                .map(MappingUtils::convertToDto)
                .collect(Collectors.toList());

        objectMapper.writeValue(response.getWriter(), exchangeRatesDto);
    }
}
