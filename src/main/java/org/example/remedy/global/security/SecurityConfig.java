package org.example.remedy.global.security;

import lombok.RequiredArgsConstructor;
import org.example.remedy.global.security.jwt.TokenFilter;
import org.example.remedy.global.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final TokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .logout(LogoutConfigurer::disable);

        http
                .cors(Customizer.withDefaults())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/health").permitAll()
                        // Admin endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        //dropping
                        .requestMatchers(HttpMethod.GET, "/droppings").permitAll()
                        .requestMatchers(HttpMethod.POST, "/droppings").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/droppings/{song-id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/droppings/{song-id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/droppings/{song-id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/droppings/playing/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/songs/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/likes/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(
                new TokenFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
