package com.ledgerly.controller;

import com.ledgerly.domain.User;
import com.ledgerly.dto.AnnualSummaryDto;
import com.ledgerly.dto.CategoryBreakdownDto;
import com.ledgerly.dto.MonthlyTrendDto;
import com.ledgerly.service.ReportService;
import com.ledgerly.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    @GetMapping("/monthly-trend")
    public ResponseEntity<List<MonthlyTrendDto>> getMonthlyTrend(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer year) {
        User user = userService.findByEmail(userDetails.getUsername());
        int y = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(reportService.getMonthlyTrend(user, y));
    }

    @GetMapping("/category-breakdown")
    public ResponseEntity<List<CategoryBreakdownDto>> getCategoryBreakdown(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(defaultValue = "EXPENSE") String type) {
        User user = userService.findByEmail(userDetails.getUsername());
        int y = year != null ? year : LocalDate.now().getYear();
        int m = month != null ? month : LocalDate.now().getMonthValue();
        return ResponseEntity.ok(reportService.getCategoryBreakdown(user, y, m, type));
    }

    @GetMapping("/annual-summary")
    public ResponseEntity<AnnualSummaryDto> getAnnualSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer year) {
        User user = userService.findByEmail(userDetails.getUsername());
        int y = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(reportService.getAnnualSummary(user, y));
    }
}
