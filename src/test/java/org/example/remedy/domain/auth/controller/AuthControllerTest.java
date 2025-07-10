package org.example.remedy.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.remedy.global.config.DroppingMockConfig;
import org.example.remedy.domain.auth.dto.AuthLoginRequest;
import org.example.remedy.domain.auth.dto.AuthRegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(DroppingMockConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("회원가입 성공")
    @Test
    void register_success() throws Exception {

        //given
        AuthRegisterRequest request = AuthRegisterRequest.builder()
                .username("sejin")
                .password("password7777")
                .email("test@gmail.com")
                .birthDate(LocalDate.of(2008, 7, 31))
                .gender(true)
                .build();

        // when & then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @DisplayName("로그인 성공")
    @Test
    void login_success() throws Exception {

        //given
        AuthRegisterRequest register = AuthRegisterRequest.builder()
                .username("sejin")
                .password("password7777")
                .email("login@gmail.com")
                .birthDate(LocalDate.of(2008, 7, 31))
                .gender(false)
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        //when
        AuthLoginRequest login = new AuthLoginRequest("password7777", "login@gmail.com");

        //then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk());
    }
}