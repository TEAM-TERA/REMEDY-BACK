package org.example.remedy.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.type.Role;
import org.example.remedy.global.config.properties.JwtProperties;
import org.example.remedy.global.security.auth.AuthDetailsService;
import org.example.remedy.global.security.jwt.exception.ExpiredJwtTokenException;
import org.example.remedy.global.security.jwt.exception.InvalidJwtTokenException;
import org.example.remedy.global.security.jwt.exception.RefreshTokenNotFoundException;
import org.example.remedy.global.security.util.CookieManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private final JwtProperties jwtProperties;
    private final AuthDetailsService authDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String ACCESS_KEY = "access_token";
    private static final String REFRESH_KEY = "refresh_token";
    private static final String REDIS_REFRESH_KEY_PREFIX = "refreshToken:";

    private SecretKey secretKey;

    @PostConstruct
    private void initKey() {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.getSecretKey()));
    }

    public String createRefreshToken(String email) {
        Date now = new Date();

        String refreshToken = Jwts.builder()
                .signWith(secretKey)
                .header()
                .add("typ", "JWT")
                .and()
                .subject(email)
                .claim("role", Role.ROLE_USER.name())
                .claim("type", REFRESH_KEY)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtProperties.getRefreshTime()))
                .compact();

        redisTemplate.opsForValue().set(
                REDIS_REFRESH_KEY_PREFIX + email,
                refreshToken,
                jwtProperties.getRefreshTime(),
                TimeUnit.SECONDS
        );

        return refreshToken;
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

    public String refresh(Cookie cookie){
        String refreshToken = cookie.getValue();
        validateTokenType(refreshToken, REFRESH_KEY);
        String email = getEmail(refreshToken);

        if(redisTemplate.opsForValue().get(REDIS_REFRESH_KEY_PREFIX+email) == null){
            throw RefreshTokenNotFoundException.EXCEPTION;
        }

        return createAccessToken(email);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtProperties.getHeader());
        if (bearerToken != null && bearerToken.startsWith(jwtProperties.getPrefix())) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        validateTokenType(token, ACCESS_KEY);
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

    private void validateTokenType(String token, String expectedType) {
        String tokenType = getTokenBody(token).get("type", String.class);
        if (!expectedType.equals(tokenType)) {
            throw InvalidJwtTokenException.EXCEPTION;
        }
    }
}