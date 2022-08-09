package com.testproject.WbPriceTrackerApi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
@Getter
public class JwtUtil {

    //from app.properties
    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expiration}")
    private long expiration;

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER = "Authorization";


    public String generateToken(String username) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(expiration).toInstant());
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);
        return jwt.getSubject();
    }

    public String getTokenPrefix() {
        return TOKEN_PREFIX;
    }

    public String getHeader() {
        return HEADER;
    }
}
