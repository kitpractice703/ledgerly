package com.ledgerly.controller;

import com.ledgerly.domain.Transaction;
import com.ledgerly.domain.User;
import com.ledgerly.dto.BudgetStatusDto;
import com.ledgerly.service.BudgetService;
import com.ledgerly.service.TransactionService;
import com.ledgerly.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final BudgetService budgetService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(defaultValue = "0") int year,
                            @RequestParam(defaultValue = "0") int month,
                            Model model) {

        // year, month 파라미터가 없으면 현재 월로 기본 조회
        if (year == 0) year = LocalDate.now().getYear();
        if (month == 0) month = LocalDate.now().getMonthValue();

        User user = userService.findByEmail(userDetails.getUsername());

        List<Transaction> transactions =
                transactionService.findByUserAndMonth(user, year, month);

        int totalIncome = transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .mapToInt(Transaction::getAmount)
                .sum();

        int totalExpense = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .mapToInt(Transaction::getAmount)
                .sum();

        List<BudgetStatusDto> budgetStatuses =
                budgetService.findBudgetStatusByUserAndMonth(user, year, month);

        model.addAttribute("transactions", transactions);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("balance", totalIncome - totalExpense);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("budgetStatuses", budgetStatuses);

        return "dashboard";
    }

}