package com.ledgerly.controller;

import com.ledgerly.domain.Category;
import com.ledgerly.repository.CategoryRepository;
import com.ledgerly.repository.TransactionRepository;
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
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("비로그인 시 거래 내역 등록 페이지 접근 불가")
    void newTransactionPage_unauthenticated_redirectToLogin() throws Exception {
        mockMvc.perform(get("/transactions/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("로그인 시 거래 내역 등록 페이지 정상 접근")
    void newTranactionPage_authenticated_success() throws Exception {
        mockMvc.perform(get("/transactions/new")
                        .with(user("test@test.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction/form"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("거래 내역 정상 등록")
    void saveTransaction_success() throws Exception {
        mockMvc.perform(post("/transactions")
                        .with(csrf())
                        .with(user("test@test.com").roles("USER"))
                        .param("categoryId", testCategory.getId().toString())
                        .param("amount", "15000")
                        .param("description", "점심식사")
                        .param("type", "EXPENSE")
                        .param("transactionDate", "2026-04-11"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        assertThat(transactionRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("거래 내역 삭제 성공")
    void deleteTransaction_success() throws Exception {
        // given
        mockMvc.perform(post("/transactions")
                .with(csrf())
                .with(user("test@test.com").roles("USER"))
                .param("categoryId", testCategory.getId().toString())
                .param("amount", "15000")
                .param("description", "점심식사")
                .param("type", "EXPENSE")
                .param("transactionDate", "2026-04-11"));

        Long transactionId = transactionRepository.findAll().get(0).getId();

        // when
        mockMvc.perform(post("/transactions/" + transactionId + "/delete")
                        .with(csrf())
                        .with(user("test@test.com").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        // then
        assertThat(transactionRepository.count()).isEqualTo(0);
    }


}