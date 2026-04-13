package com.ledgerly.dto;

import com.ledgerly.domain.Budget;
import lombok.Getter;

@Getter
public class BudgetStatusDto {

    private final Budget budget;
    private final int spentAmount;
    private final int remaining;
    private final boolean exceeded;

    public BudgetStatusDto(Budget budget, int spentAmount) {
        this.budget = budget;
        this.spentAmount = spentAmount;
        this.remaining = budget.getLimitAmount() - spentAmount;
        this.exceeded = spentAmount >= budget.getLimitAmount();
    }
}
