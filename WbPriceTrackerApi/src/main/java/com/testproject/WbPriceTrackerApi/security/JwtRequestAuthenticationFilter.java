package com.testproject.WbPriceTrackerApi.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.testproject.WbPriceTrackerApi.exception.ExceptionMessage;
import com.testproject.WbPriceTrackerApi.exception.MessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtRequestAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public JwtRequestAuthenticationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtUtil = jwtUtil;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    // Jwt Authentication Filter checks if the request has a valid JWT token
    // If it has a valid JWT Token then it sets the Authentication in the context
    // which means the current user is authenticated
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //Authorization header, where the JWT token should be
        String authHeader = request.getHeader(jwtUtil.getHeader());
        // If header is present and contains prefix Bearer
        if (authHeader != null && authHeader.startsWith(jwtUtil.getTokenPrefix())) {
            // Get token from header
            String jwt = authHeader.replace(jwtUtil.getTokenPrefix(), "");
            // If token is present, check for validity and get claim
            try {
                String username = jwtUtil.validateToken(jwt);
                // Get principal by username
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
                // Create auth object, which needs principal, credentials and list of authorities
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        userDetails.getPassword(),
                        userDetails.getAuthorities());
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Authenticate the user
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JWTVerificationException e) {
                log.info("Invalid JWT Token in Authorization Header : {}", jwt);
                ResponseWriterUtil.writeResponse(response,
                        ExceptionMessage.setMessage(MessageConstant.INVALID_JWT_TOKEN),
                        HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        // Go to the next filter in the filter chain
        filterChain.doFilter(request, response);
    }
}
