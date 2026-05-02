package com.ledgerly.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnnualSummaryDto {
    private final long totalIncome;
    private final long totalExpense;
    private final long netSavings;
    private final int savingsRate;
}
