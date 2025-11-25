package org.example.remedy.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.domain.Role;
import org.example.remedy.global.properties.JwtProperties;
import org.example.remedy.global.security.auth.AuthDetailsService;
import org.example.remedy.global.security.jwt.exception.ExpiredJwtTokenException;
import org.example.remedy.global.security.jwt.exception.InvalidJwtTokenException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private final JwtProperties jwtProperties;
    private final AuthDetailsService authDetailsService;

    private static final String ACCESS_KEY = "access_token";

    private SecretKey secretKey;

    @PostConstruct
    private void initKey() {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.getSecretKey()));
    }

    public String createAccessToken(String email) {
        Date now = new Date();

        return Jwts.builder()
                .signWith(secretKey)
                .header()
                .add("typ", "JWT")
                .and()
                .subject(email)
                .claim("role", Role.ROLE_USER.name())
                .claim("type", ACCESS_KEY)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtProperties.getAccessTime()))
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtProperties.getHeader());
        if (bearerToken != null && bearerToken.startsWith(jwtProperties.getPrefix())) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        validateAccessTokenType(token);
        UserDetails userDetails = authDetailsService.loadUserByUsername(getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getEmail(String token) {
        return getTokenBody(token).getSubject();
    }

    private Claims getTokenBody(String token) {
        try {
            SecretKey key = secretKey;
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw ExpiredJwtTokenException.EXCEPTION;
        } catch (JwtException e) {
            throw InvalidJwtTokenException.EXCEPTION;
        }
    }

    private void validateAccessTokenType(String token) {
        String tokenType = getTokenBody(token).get("type", String.class);
        if (!ACCESS_KEY.equals(tokenType)) {
            throw InvalidJwtTokenException.EXCEPTION;
        }
    }
}
