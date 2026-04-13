package com.ledgerly.service;

import com.ledgerly.domain.Budget;
import com.ledgerly.domain.Category;
import com.ledgerly.domain.User;
import com.ledgerly.dto.BudgetStatusDto;
import com.ledgerly.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryService categoryService;
    private final TransactionService transactionService;

    @Transactional
    public Budget save(
            User user,
            Long categoryId,
            Integer limitAmount,
            Integer year,
            Integer month
    ) {
        // 동일 사용자·카테고리·연월 조합의 예산 중복 등록 방지
        budgetRepository.findByUserAndCategoryIdAndYearAndMonth(
                user,
                categoryId,
                year,
                month
        ).ifPresent(b -> {
            throw new IllegalArgumentException("이미 해당 카테고리의 예산이 존재합니다.");
        });

        Category category = categoryService.findById(categoryId);

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setLimitAmount(limitAmount);
        budget.setYear(year);
        budget.setMonth(month);

        return budgetRepository.save(budget);
    }

    @Transactional(readOnly = true)
    public List<Budget> findByUserAndMonth(
            User user,
            Integer year,
            Integer month
    ) {
        return budgetRepository.findByUserAndYearAndMonth(user, year, month);
    }

    @Transactional(readOnly = true)
    public List<BudgetStatusDto> findBudgetStatusByUserAndMonth(
            User user, Integer year, Integer month
    ) {
        List<Budget> budgets = budgetRepository.findByUserAndYearAndMonth(user, year, month);

        return budgets.stream()
                .map(budget -> {
                    int spent = transactionService.sumByUserAndCategoryAndMonth(
                            user, budget.getCategory().getId(), "EXPENSE", year, month
                    );
                    return new BudgetStatusDto(budget, spent);
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Transactional
    public void update(Long budgetId, User user, Integer limitAmount) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예산입니다."));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        budget.setLimitAmount(limitAmount);
    }

    @Transactional
    public void delete(Long budgetId, User user) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예산입니다."));

        // 타인의 예산 삭제 시도 차단
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        budgetRepository.delete(budget);
    }

    @Transactional(readOnly = true)
    public boolean isExceeded(
            User user,
            Long categoryId,
            Integer year,
            Integer month
    ) {
        return budgetRepository
                .findByUserAndCategoryIdAndYearAndMonth(user, categoryId, year, month)
                .map(budget -> {
                    int spent = transactionService.sumByUserAndCategoryAndMonth(
                            user, categoryId, "EXPENSE", year, month
                    );
                    return spent >= budget.getLimitAmount();
                })
                .orElse(false);
    }
}
