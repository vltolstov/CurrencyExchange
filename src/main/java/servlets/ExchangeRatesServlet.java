package servlets;

import Utils.MappingUtils;
import Utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRateDao;
import dao.JdbcExchangeRateDao;
import dto.ExchangeRateRequestDto;
import dto.ExchangeRateResponseDto;
import exceptions.InvalidParameterException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ExchangeRate;
import services.ExchangeRateService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();

        List<ExchangeRateResponseDto> exchangeRatesDto = exchangeRates.stream()
                .map(MappingUtils::convertToDto)
                .collect(Collectors.toList());

        objectMapper.writeValue(response.getWriter(), exchangeRatesDto);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // получаем данные из запроса
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rate = request.getParameter("rate");

        // проверяем корректность rate
        if(rate == null || rate.isBlank()) {
            throw new InvalidParameterException("Rate is required");
        }

        //создаем DTO из полученных данных
        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, convertRateToDouble(rate));

        //валидируем DTO
        ValidationUtils.validate(exchangeRateRequestDto);

        //получаем новую сущность
        // - сервис инициализиует сущность и передает в DAO
        // - DAO сохраняет в базу и возвращает сущность
        ExchangeRate exchangeRate = exchangeRateService.save(exchangeRateRequestDto);

        //устанавливаем статус если все ок
        response.setStatus(HttpServletResponse.SC_CREATED);

        //сериализуем в json
        objectMapper.writeValue(response.getWriter(), exchangeRate);
    }

    private Double convertRateToDouble(String rate) {
        try {
            return Double.parseDouble(rate);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Rate is not a number");
        }
    }
}
