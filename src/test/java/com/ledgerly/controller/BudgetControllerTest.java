package com.ledgerly.controller;

import com.ledgerly.domain.Category;
import com.ledgerly.repository.BudgetRepository;
import com.ledgerly.repository.CategoryRepository;
import com.ledgerly.repository.UserRepository;
import com.ledgerly.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        userService.register("test@test.com", "password123", "김인태");

        testCategory = new Category();
        testCategory.setName("식비");
        testCategory.setType("EXPENSE");
        categoryRepository.save(testCategory);
    }


    @AfterEach
    void tearDown() {
        budgetRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("예산 관리 페이지 정상 접근")
    void budgetPage_authenticated_success() throws Exception {
        mockMvc.perform(get("/budgets")
                        .with(user("test@test.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("budget/list"))
                .andExpect(model().attributeExists("budgetStatuses", "categories"));
    }

    @Test
    @DisplayName("예산 등록 성공 시 리다이렉트")
    void saveBudget_success_redirectToBudgets() throws Exception {
        mockMvc.perform(post("/budgets")
                        .with(csrf())
                        .with(user("test@test.com").roles("USER"))
                        .param("categoryId", testCategory.getId().toString())
                        .param("limitAmount", "300000")
                        .param("year", "2026")
                        .param("month", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/budgets"));

        assertThat(budgetRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("같은 카테고리 예산 중복 등록 시 에러 메시지 반환")
    void saveBudget_duplicate_returnsError() throws Exception {
        // given
        mockMvc.perform(post("/budgets")
                .with(csrf())
                .with(user("test@test.com").roles("USER"))
                .param("categoryId", testCategory.getId().toString())
                .param("limitAmount", "300000")
                .param("year", "2026")
                .param("month", "4"));

        // when
        mockMvc.perform(post("/budgets")
                        .with(csrf())
                        .with(user("test@test.com").roles("USER"))
                        .param("categoryId", testCategory.getId().toString())
                        .param("limitAmount", "500000")
                        .param("year", "2026")
                        .param("month", "4"))
                .andExpect(status().isOk())
                .andExpect(view().name("budget/list"))
                .andExpect(model().attributeExists("error"));

        // then
        assertThat(budgetRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("비로그인 시 예산 페이지 접근 불가")
    void budgetPage_unauthenticated_redirectToLogin() throws Exception {
        mockMvc.perform(get("/budgets"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}