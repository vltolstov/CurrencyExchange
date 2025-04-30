package servlets;

import Utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import dao.JdbcCurrencyDao;
import exceptions.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;

import java.io.IOException;

import static Utils.MappingUtils.convertToDto;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyDao currencyDao = new JdbcCurrencyDao();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //получаем код
        String code = request.getPathInfo().replaceFirst("/", "");

        //валидируем
        ValidationUtils.validateCurrencyCode(code);

        //достаем из базы
        Currency currency = currencyDao.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Currency with code " + code + " not found"));

        //сериализуем в json
        objectMapper.writeValue(response.getWriter(), convertToDto(currency));
    }
}
