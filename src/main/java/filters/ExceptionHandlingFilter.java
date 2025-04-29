package filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ErrorResponseDto;
import exceptions.DatabaseOperationException;
import exceptions.EntityExistException;
import exceptions.InvalidParameterException;
import exceptions.NotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@WebFilter("/*")
public class ExceptionHandlingFilter extends HttpFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            super.doFilter(request, response, chain);
        }
        catch (DatabaseOperationException e){
            writeErrorResponse(response, SC_INTERNAL_SERVER_ERROR, e);
        }
        catch (EntityExistException e) {
            writeErrorResponse(response, SC_CONFLICT, e);
        }
        catch (InvalidParameterException e) {
            writeErrorResponse(response,SC_BAD_REQUEST, e);
        }
        catch (NotFoundException e) {
            writeErrorResponse(response, SC_NOT_FOUND, e);
        }
    }

    private void writeErrorResponse(HttpServletResponse response, int errorCode, RuntimeException e) throws IOException {

        response.setStatus(errorCode);

        objectMapper.writeValue(response.getWriter(), new ErrorResponseDto(
                errorCode,
                e.getMessage()
        ));
    }
}
