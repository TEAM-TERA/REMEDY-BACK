package org.example.remedy.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.remedy.domain.auth.config.DroppingMockConfig;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.global.security.auth.AuthDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(DroppingMockConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseUrl = "/users";
    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.testUser("sejin", "test@example.com"));

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
                .andExpect(jsonPath("$.profileImageUrl").value("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfODMg/MDAxNjA0MjI4ODc1MDgz.gQ3xcHrLXaZyxcFAoEcdB7tJWuRs7fKgOxQwPvsTsrUg.0OBtKHq2r3smX5guFQtnT7EDwjzksz5Js0wCV4zjfpcg.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%EB%B3%B4%EB%9D%BC.jpg?type=w400"));
    }

    @Test
    @DisplayName("프로필 수정 성공")
    void updateProfile_success() throws Exception {
        //given
        UserProfileUpdateRequest request = new UserProfileUpdateRequest("newName", Boolean.FALSE,null);
        String json = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(patch(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        //then
        User updatedUser = userRepository.findByEmail("test@example.com").orElseThrow();
        assertThat(updatedUser.getUsername()).isEqualTo("newName");
        assertThat(updatedUser.isGender()).isFalse();
    }

}
