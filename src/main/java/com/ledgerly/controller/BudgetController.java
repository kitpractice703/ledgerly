package com.ledgerly.controller;

import com.ledgerly.domain.Budget;
import com.ledgerly.domain.User;
import com.ledgerly.dto.BudgetRequestDto;
import com.ledgerly.dto.BudgetStatusDto;
import com.ledgerly.service.BudgetService;
import com.ledgerly.service.CategoryService;
import com.ledgerly.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping
    public String budgetPage(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(defaultValue = "0") int year,
                             @RequestParam(defaultValue = "0") int month,
                             Model model) {

        if (year == 0) year = LocalDate.now().getYear();
        if (month == 0) month = LocalDate.now().getMonthValue();

        User user = userService.findByEmail(userDetails.getUsername());
        List<BudgetStatusDto> budgetStatuses = budgetService.findBudgetStatusByUserAndMonth(user, year, month);

        model.addAttribute("budgetStatuses", budgetStatuses);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("year", year);
        model.addAttribute("month", month);

        return "budget/list";
    }

    @PostMapping
    public String save(@AuthenticationPrincipal UserDetails userDetails,
                       @Valid BudgetRequestDto dto,
                       BindingResult bindingResult,
                       Model model) {

        if (bindingResult.hasErrors()) {
            int year = dto.getYear() != null ? dto.getYear() : LocalDate.now().getYear();
            int month = dto.getMonth() != null ? dto.getMonth() : LocalDate.now().getMonthValue();
            User user = userService.findByEmail(userDetails.getUsername());
            List<Budget> budgets = budgetService.findByUserAndMonth(user, year, month);

            Map<Long, Boolean> budgetExceeded = new HashMap<>();
            for (Budget budget : budgets) {
                budgetExceeded.put(budget.getId(),
                        budgetService.isExceeded(user, budget.getCategory().getId(), year, month));
            }

            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("budgets", budgets);
            model.addAttribute("budgetExceeded", budgetExceeded);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("year", year);
            model.addAttribute("month", month);
            return "budget/list";
        }

        User user = userService.findByEmail(userDetails.getUsername());

        try {
            budgetService.save(user, dto.getCategoryId(), dto.getLimitAmount(), dto.getYear(), dto.getMonth());
            return "redirect:/budgets";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            return "budget/list";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Long id,
                         @RequestParam Integer limitAmount) {

        User user = userService.findByEmail(userDetails.getUsername());

        try {
            budgetService.update(id, user, limitAmount);
        } catch (IllegalArgumentException e) {

        }
        return "redirect:/budgets";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Long id) {

        User user = userService.findByEmail(userDetails.getUsername());
        budgetService.delete(id, user);

        return "redirect:/budgets";
    }
}
