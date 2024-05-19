package com.scottishpower.smartmeter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scottishpower.smartmeter.enums.Error;
import com.scottishpower.smartmeter.exception.ErrorDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ErrorDetails errorDetails = new ErrorDetails(Error.FORBIDDEN_EXCEPTION.getCode(), Error.FORBIDDEN_EXCEPTION.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}
