package com.testproject.WbPriceTrackerApi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testproject.WbPriceTrackerApi.dto.AuthDto;
import com.testproject.WbPriceTrackerApi.exception.RequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JwtUsernamePasswordAuthenticationFilter(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    // Triggered on POST request to /login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            // Get credentials from request
            AuthDto credentials = new ObjectMapper().readValue(request.getInputStream(), AuthDto.class);
            // Create login token
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken (
                    credentials.getUsername(), credentials.getPassword());
            // Authenticate user by authentication manager
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            log.warn("I/O error : {}", e.getMessage());
            throw new RequestException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
       UserDetailsImpl principal = (UserDetailsImpl) auth.getPrincipal();
        // Token generation after successful authentication
        String token = jwtUtil.generateToken(principal.getUsername());
        log.info("Successful authentication of user : {}", principal.getUsername());
        // Add token in response header
        response.addHeader(jwtUtil.getHeader(), jwtUtil.getTokenPrefix() + token);
    }
}
