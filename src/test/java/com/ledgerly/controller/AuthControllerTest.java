package com.ledgerly.controller;

import com.ledgerly.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 페이지 정상 접근")
    void loginPage_success() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("회원가입 페이지 정상 접근")
    void registerPage_success() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    @DisplayName("회원가입 성공시 로그인 페이지로 리다이렉트")
    void register_success_redirectToLogin() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", "test@test.com")
                        .param("password", "password123")
                        .param("username", "김인태"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertThat(userRepository.existsByEmail("test@test.com")).isTrue();
    }

    @Test
    @DisplayName("이메일 중복 시 회원가입 페이지로 돌아옴")
    void register_duplicateEmail_returnsRegisterPage() throws Exception {
        // given
        mockMvc.perform(post("/register")
                .with(csrf())
                .param("email", "test@test.com")
                .param("password", "password123")
                .param("username", "김인태"));

        // when
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", "test@test.com")
                        .param("password", "password456")
                        .param("username", "홍길동"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("error"));
    }
}