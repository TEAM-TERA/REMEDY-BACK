package org.example.remedy.domain.user.controller;

import jakarta.transaction.Transactional;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.domain.user.type.Provider;
import org.example.remedy.domain.user.type.Role;
import org.example.remedy.global.security.auth.AuthDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final String baseUrl = "/users";
    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .username("sejin")
                .password("password7777")
                .email("test@example.com")
                .profileImage("https://image.com/profile.png")
                .birthdate(LocalDate.of(2008, 7, 31))
                .gender(true)
                .role(Role.ROLE_USER)
                .provider(Provider.SELF)
                .build());

        AuthDetails authDetails = new AuthDetails(user);
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(authDetails, null, "ROLE_USER"));
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void getMyProfile_success() throws Exception {
        //when
        mockMvc.perform(get(baseUrl))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("sejin"))
                .andExpect(jsonPath("$.profileImageUrl").value("https://image.com/profile.png"));
    }
}
