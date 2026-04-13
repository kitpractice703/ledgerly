package com.ledgerly.repository;


import com.ledgerly.domain.Category;
import com.ledgerly.domain.Transaction;
import com.ledgerly.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest // JPA 관련 컴포넌트만 로드
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("월별 거래 내역 조회")
    void findByUserAndMonth() {
        // given
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setUsername("김인태");
        userRepository.save(user);

        Category category = new Category();
        category.setName("식비");
        category.setType("EXPENSE");
        categoryRepository.save(category);

        Transaction t1 = new Transaction();
        t1.setUser(user);
        t1.setCategory(category);
        t1.setAmount(15000);
        t1.setType("EXPENSE");
        t1.setTransactionDate(LocalDate.of(2026, 4, 5));
        transactionRepository.save(t1);

        Transaction t2 = new Transaction();
        t2.setUser(user);
        t2.setCategory(category);
        t2.setAmount(8000);
        t2.setType("EXPENSE");
        t2.setTransactionDate(LocalDate.of(2026, 3, 15));
        transactionRepository.save(t2);

        // when
        List<Transaction> result = transactionRepository
                .findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(
                        user,
                        LocalDate.of(2026, 4, 1),
                        LocalDate.of(2026, 4, 30)
                );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualTo(15000);
    }

}