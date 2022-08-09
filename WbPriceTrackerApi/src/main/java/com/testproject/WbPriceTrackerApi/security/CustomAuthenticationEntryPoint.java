package com.testproject.WbPriceTrackerApi.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws ServletException {
        log.info("Authentication entry point called. Rejecting access");
        ResponseWriterUtil.writeResponse(response,
                "Unauthorized: authentication required",
                HttpServletResponse.SC_UNAUTHORIZED);
    }
}
