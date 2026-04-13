package com.ledgerly.service;


import com.ledgerly.domain.Budget;
import com.ledgerly.domain.Category;
import com.ledgerly.domain.User;
import com.ledgerly.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private BudgetService budgetService;

    private User testUser;
    private Category testcategory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");

        testcategory = new Category();
        testcategory.setName("식비");
        testcategory.setType("EXPENSE");
    }

    @Test
    @DisplayName("예산 중복 등록 시 예외 발생")
    void save_duplicateBudget_throwsException() {
        // given
        Budget existingBudget = new Budget();
        when(budgetRepository.findByUserAndCategoryIdAndYearAndMonth(
                testUser, 1L, 2026, 4
        )).thenReturn(Optional.of(existingBudget)); // 이미 예산이 존재

        // when, then
        assertThatThrownBy(() -> budgetService.save(testUser, 1L, 300000, 2026, 4)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 해당 카테고리의 예산이 존재합니다.");
    }

    @Test
    @DisplayName("지출이 예산 초과 시 true 반환")
    void isExceeded_overBudget_returnsTrue() {
        // given
        Budget budget = new Budget();
        budget.setLimitAmount(300000);

        when(budgetRepository.findByUserAndCategoryIdAndYearAndMonth(
                testUser, 1L, 2026, 4
        )).thenReturn(Optional.of(budget));

        when(transactionService.sumByUserAndCategoryAndMonth(
                testUser, 1L, "EXPENSE", 2026, 4
        )).thenReturn(320000); // 실제 지출 32만원 (초과)

        // when
        boolean result = budgetService.isExceeded(testUser, 1L, 2026, 4);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("지출이 예산 미만 시 false 반환")
    void isExceeded_underBudget_returnsFalse() {
        // given
        Budget budget = new Budget();
        budget.setLimitAmount(300000);

        when(budgetRepository.findByUserAndCategoryIdAndYearAndMonth(
                testUser, 1L, 2026, 4
        )).thenReturn(Optional.of(budget));

        when(transactionService.sumByUserAndCategoryAndMonth(
                testUser, 1L, "EXPENSE", 2026, 4
        )).thenReturn(150000); // 실제 지출 15만원

        // when
        boolean result = budgetService.isExceeded(testUser, 1L, 2026, 4);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("예산 미설정 시 초과 아님")
    void isExceeded_noBudget_returnsFalse() {
        // given
        when(budgetRepository.findByUserAndCategoryIdAndYearAndMonth(
                any(), any(), any(), any()
        )).thenReturn(Optional.empty());

        // when
        boolean result = budgetService.isExceeded(testUser, 1L, 2026, 4);

        // then
        assertThat(result).isFalse();
    }


}