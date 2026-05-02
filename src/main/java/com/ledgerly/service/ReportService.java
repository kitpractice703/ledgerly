package com.ledgerly.service;

import com.ledgerly.domain.User;
import com.ledgerly.dto.AnnualSummaryDto;
import com.ledgerly.dto.CategoryBreakdownDto;
import com.ledgerly.dto.MonthlyTrendDto;
import com.ledgerly.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<MonthlyTrendDto> getMonthlyTrend(User user, int year) {
        List<Object[]> raw = transactionRepository.findMonthlyTrend(user, year);

        Map<Integer, MonthlyTrendDto> map = new LinkedHashMap<>();
        for (int m = 1; m <= 12; m++) {
            map.put(m, new MonthlyTrendDto(m, 0L, 0L));
        }

        for (Object[] row : raw) {
            int month = ((Number) row[0]).intValue();
            String type = (String) row[1];
            long amount = ((Number) row[2]).longValue();
            MonthlyTrendDto dto = map.get(month);
            if ("INCOME".equals(type)) dto.setIncome(amount);
            else dto.setExpense(amount);
        }

        return new ArrayList<>(map.values());
    }

    @Transactional(readOnly = true)
    public List<CategoryBreakdownDto> getCategoryBreakdown(User user, int year, int month, String type) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<Object[]> raw = transactionRepository.findCategoryBreakdown(user, type, startDate, endDate);

        return raw.stream()
                .map(row -> new CategoryBreakdownDto((String) row[0], ((Number) row[1]).longValue()))
                .toList();
    }

    @Transactional(readOnly = true)
    public AnnualSummaryDto getAnnualSummary(User user, int year) {
        List<Object[]> raw = transactionRepository.findAnnualSummary(user, year);

        long totalIncome = 0L;
        long totalExpense = 0L;
        for (Object[] row : raw) {
            String type = (String) row[0];
            long amount = ((Number) row[1]).longValue();
            if ("INCOME".equals(type)) totalIncome = amount;
            else totalExpense = amount;
        }

        long netSavings = totalIncome - totalExpense;
        int savingsRate = totalIncome == 0 ? 0 : (int) (netSavings * 100 / totalIncome);

        return new AnnualSummaryDto(totalIncome, totalExpense, netSavings, savingsRate);
    }
}
