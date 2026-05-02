package com.ledgerly.controller;

import com.ledgerly.domain.User;
import com.ledgerly.dto.BudgetRequestDto;
import com.ledgerly.dto.BudgetStatusDto;
import com.ledgerly.service.BudgetService;
import com.ledgerly.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<BudgetStatusDto>> findAll(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {

        if (year == 0) year = LocalDate.now().getYear();
        if (month == 0) month = LocalDate.now().getMonthValue();

        User user = userService.findByEmail(userDetails.getUsername());
        List<BudgetStatusDto> budgetStatuses = budgetService.findBudgetStatusByUserAndMonth(user, year, month);

        return ResponseEntity.ok(budgetStatuses);
    }

    @PostMapping
    public ResponseEntity<?> save(@AuthenticationPrincipal UserDetails userDetails,
                                  @Valid @RequestBody BudgetRequestDto dto,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        User user = userService.findByEmail(userDetails.getUsername());

        try {
            budgetService.save(user, dto.getCategoryId(), dto.getLimitAmount(), dto.getYear(), dto.getMonth());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable Long id,
                                    @RequestParam Integer limitAmount) {
        User user = userService.findByEmail(userDetails.getUsername());

        try {
            budgetService.update(id, user, limitAmount);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable Long id) {
        User user = userService.findByEmail(userDetails.getUsername());
        budgetService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
