package com.kakaopay.homework.support;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private Key encodedSecretKey;
    private long aliveDurationMilli;

    public JwtTokenProvider(@Value("${security.jwt.token.secretKey}") final String secretKey,
                            @Value("${security.jwt.token.aliveDurationMilli}") final long aliveDurationMilli) {
        this.encodedSecretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encode(secretKey.getBytes()));
        this.aliveDurationMilli = aliveDurationMilli;
    }

    public String createToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);

        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + aliveDurationMilli);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(encodedSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(encodedSecretKey).parseClaimsJws(token);
            return claims.getBody().getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
