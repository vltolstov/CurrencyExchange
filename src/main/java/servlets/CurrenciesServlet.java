package servlets;

import Utils.MappingUtils;
import Utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import dao.JdbcCurrencyDao;
import dto.CurrencyRequestDto;
import dto.CurrencyResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static Utils.MappingUtils.convertToDto;

@WebServlet("/currencies/")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyDao currencyDao = new JdbcCurrencyDao();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Currency> currencies = currencyDao.findAll();

        List<CurrencyResponseDto> currenciesDto = currencies.stream()
                .map(MappingUtils::convertToDto)
                .collect(Collectors.toList());

        objectMapper.writeValue(response.getWriter(), currenciesDto);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //получаем данные из запроса
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

        //создаем дто
        CurrencyRequestDto currencyRequestDto = new CurrencyRequestDto(name, code, sign);

        //валидируем
        ValidationUtils.validate(currencyRequestDto);

        //преобразуем с помощью маппера дто в модель
        Currency currency = currencyDao.save(MappingUtils.convertToEntity(currencyRequestDto));

        //устанавливаем код
        response.setStatus(HttpServletResponse.SC_CREATED);

        //сериализуем в json дто из модели
        objectMapper.writeValue(response.getWriter(), convertToDto(currency));
    }
}
