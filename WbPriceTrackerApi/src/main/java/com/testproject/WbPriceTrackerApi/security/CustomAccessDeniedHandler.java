package com.testproject.WbPriceTrackerApi.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws ServletException {
        log.info("Authorization Error. Access is denied. No access permissions to the resource.");
        ResponseWriterUtil.writeResponse(response,
                "You do not have permissions to access the resource",
                HttpServletResponse.SC_FORBIDDEN);
    }
}
