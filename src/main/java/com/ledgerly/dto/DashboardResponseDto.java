package com.ledgerly.dto;

import com.ledgerly.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardResponseDto {

    private int year;
    private int month;
    private int totalIncome;
    private int totalExpense;
    private int balance;
    private List<Transaction> transactions;
    private List<BudgetStatusDto> budgetStatuses;
}
