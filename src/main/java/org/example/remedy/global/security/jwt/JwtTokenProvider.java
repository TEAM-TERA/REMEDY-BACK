package org.example.remedy.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.type.Role;
import org.example.remedy.global.config.properties.JwtProperties;
import org.example.remedy.global.security.auth.AuthDetailsService;
import org.example.remedy.global.security.jwt.exception.ExpiredJwtTokenException;
import org.example.remedy.global.security.jwt.exception.InvalidJwtTokenException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
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
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;
    private final AuthDetailsService authDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String ACCESS_KEY = "access_token";
    private static final String REFRESH_KEY = "refresh_token";

    public void createTokens(String email, HttpServletResponse response) {
        String accessToken = createToken(email, ACCESS_KEY, jwtProperties.getAccessTime());
        String refreshToken = createToken(email, REFRESH_KEY, jwtProperties.getRefreshTime());

        redisTemplate.opsForValue().set(
                email,
                refreshToken,
                jwtProperties.getRefreshTime(),
                TimeUnit.SECONDS
        );

        response.setHeader("Authorization", "Bearer " + accessToken);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_KEY, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge((int) (jwtProperties.getRefreshTime()))
                .sameSite("None")
                .build();

        redisTemplate.opsForValue().set("refreshToken:" + email, accessToken, jwtProperties.getRefreshTime(), TimeUnit.SECONDS);

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private String createToken(String email, String type, Long time) {
        Date now = new Date();
        SecretKey key = Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(jwtProperties.getSecretKey())
        );

        return Jwts.builder()
                .signWith(key)
                .header()
                .add("typ", "JWT")
                .and()
                .subject(email)
                .claim("role", Role.ROLE_USER.name())
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + time))
                .compact();
    }

    public void deleteTokens(HttpServletResponse response) {
        response.setHeader("Authorization", "");
        
        ResponseCookie cookie = ResponseCookie.from(REFRESH_KEY, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
            
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void refresh(Cookie cookie, HttpServletResponse response){
        String refreshToken = cookie.getValue();
        validateTokenType(refreshToken, REFRESH_KEY);
        String email = getEmail(refreshToken);
        redisTemplate.opsForValue().get("refreshToken:"+email);


        String accessToken = createToken(email, ACCESS_KEY, jwtProperties.getAccessTime());
        response.setHeader("Authorization", "Bearer " + accessToken);
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
            SecretKey key = Keys.hmacShaKeyFor(
                    Base64.getDecoder().decode(jwtProperties.getSecretKey())
            );

            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw ExpiredJwtTokenException.EXCEPTION;
        } catch (Exception e) {
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