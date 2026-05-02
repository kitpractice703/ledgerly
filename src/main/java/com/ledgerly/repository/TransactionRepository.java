package com.ledgerly.repository;

import com.ledgerly.domain.Transaction;
import com.ledgerly.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    // 카테고리·타입·기간 조건의 금액 합계 — 예산 초과 여부 계산에 사용
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.user = :user " +
            "AND t.category.id = :categoryId " +
            "AND t.type = :type " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    Integer sumAmountByUserAndCategoryAndTypeDateBetween(
            @Param("user") User user,
            @Param("categoryId") Long category,
            @Param("type") String type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT MONTH(t.transactionDate), t.type, SUM(t.amount) FROM Transaction t " +
            "WHERE t.user = :user AND YEAR(t.transactionDate) = :year " +
            "GROUP BY MONTH(t.transactionDate), t.type " +
            "ORDER BY MONTH(t.transactionDate)")
    List<Object[]> findMonthlyTrend(
            @Param("user") User user,
            @Param("year") int year
    );

    @Query("SELECT t.category.name, SUM(t.amount) FROM Transaction t " +
            "WHERE t.user = :user AND t.type = :type " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY t.category.id, t.category.name " +
            "ORDER BY SUM(t.amount) DESC")
    List<Object[]> findCategoryBreakdown(
            @Param("user") User user,
            @Param("type") String type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT t.type, SUM(t.amount) FROM Transaction t " +
            "WHERE t.user = :user AND YEAR(t.transactionDate) = :year " +
            "GROUP BY t.type")
    List<Object[]> findAnnualSummary(
            @Param("user") User user,
            @Param("year") int year
    );
}


