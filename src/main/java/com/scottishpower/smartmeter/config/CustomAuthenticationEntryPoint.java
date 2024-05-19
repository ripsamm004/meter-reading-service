package com.scottishpower.smartmeter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scottishpower.smartmeter.enums.Error;
import com.scottishpower.smartmeter.exception.ErrorDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ErrorDetails errorDetails = new ErrorDetails(Error.UNAUTHORIZED_EXCEPTION.getCode(), Error.UNAUTHORIZED_EXCEPTION.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}
