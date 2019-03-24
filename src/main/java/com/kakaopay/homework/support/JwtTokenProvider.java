package com.kakaopay.homework.support;

import com.kakaopay.homework.dao.UserRepository;
import com.kakaopay.homework.domain.entity.User;
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
    private UserRepository userRepository;

    public JwtTokenProvider(@Value("${security.jwt.token.secretKey}") final String secretKey,
                            @Value("${security.jwt.token.aliveDurationMilli}") final long aliveDurationMilli,
                            final UserRepository userRepository) {
        this.encodedSecretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encode(secretKey.getBytes()));
        this.aliveDurationMilli = aliveDurationMilli;
        this.userRepository = userRepository;
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
        log.info("token = {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public String getUserId(String token) {
        return Jwts.parser().setSigningKey(encodedSecretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(encodedSecretKey).parseClaimsJws(token);
            Claims body = claims.getBody();
            if (body.getExpiration().before(new Date())) {
                return false;
            }
            User user = userRepository.findOne(body.getSubject());
            return user != null;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
