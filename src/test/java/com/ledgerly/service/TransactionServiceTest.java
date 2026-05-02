package com.ledgerly.service;

import com.ledgerly.domain.Category;
import com.ledgerly.domain.Transaction;
import com.ledgerly.domain.User;
import com.ledgerly.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setUsername("김인태");

        testCategory = new Category();
        testCategory.setName("식비");
        testCategory.setType("EXPENSE");
    }

    @Test
    @DisplayName("거래 내역 정상 등록")
    void save_success() {
        // given
        when(categoryService.findById(1L, testUser)).thenReturn(testCategory);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(i -> i.getArgument(0));

        // when
        Transaction result = transactionService.save(
                testUser, 1L, 15000, "점심식사", "EXPENSE", LocalDate.now()
        );

        // then
        assertThat(result.getAmount()).isEqualTo(15000);
        assertThat(result.getDescription()).isEqualTo("점심식사");
        assertThat(result.getType()).isEqualTo("EXPENSE");
        assertThat(result.getUser()).isEqualTo(testUser);
    }

    @Test
    @DisplayName("타인의 거래 내역 삭제시 예외 발생")
    void delete_unauthorizedUser_throwsException() {
        // given
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@test.com");

        Transaction transaction = new Transaction();
        transaction.setUser(otherUser); // 타인 거래내역

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // when
        assertThatThrownBy(() -> transactionService.delete(1L, testUser) // 내가 삭제 시도
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("삭제 권한이 없습니다.");

        // then
        verify(transactionRepository, never()).delete(any()); // 삭제 유무 확인
    }

    @Test
    @DisplayName("거래 없을 때 합계는 0")
    void sum_noTransaction_returnZero() {
        // given
        when(transactionRepository.sumAmountByUserAndCategoryAndTypeDateBetween(
                any(), any(), any(), any(), any()
        )).thenReturn(null);

        // when
        int result = transactionService.sumByUserAndCategoryAndMonth(
                testUser, 1L, "EXPENSE", 2026, 4
        );

        // then
        assertThat(result).isEqualTo(0); // null을 0으로 처리했는지 확인
    }

}