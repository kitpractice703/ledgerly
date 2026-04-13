package com.ledgerly.controller;

import com.ledgerly.domain.Transaction;
import com.ledgerly.domain.User;
import com.ledgerly.dto.TransactionRequestDto;
import com.ledgerly.service.CategoryService;
import com.ledgerly.service.TransactionService;
import com.ledgerly.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping("/new")
    public String newTransactionPage(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "transaction/form";
    }

    @PostMapping
    public String save(@AuthenticationPrincipal UserDetails userDetails,
                       @Valid TransactionRequestDto dto,
                       BindingResult bindingResult,
                       Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("categories", categoryService.findAll());
            return "transaction/form";
        }

        User user = userService.findByEmail(userDetails.getUsername());
        transactionService.save(user, dto.getCategoryId(), dto.getAmount(), dto.getDescription(), dto.getType(), dto.getTransactionDate());

        return "redirect:/dashboard";
    }

    @GetMapping("/{id}/edit")
    public String editTransactionPage(@AuthenticationPrincipal UserDetails userDetails,
                                      @PathVariable Long id,
                                      Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        Transaction transaction = transactionService.findById(id, user);
        model.addAttribute("transaction", transaction);
        model.addAttribute("categories", categoryService.findAll());
        return "transaction/edit";
    }

    @PostMapping("/{id}/update")
    public String update(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Long id,
                         @Valid TransactionRequestDto dto,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            User user = userService.findByEmail(userDetails.getUsername());
            model.addAttribute("transaction", transactionService.findById(id, user));
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "transaction/edit";
        }

        User user = userService.findByEmail(userDetails.getUsername());
        transactionService.update(id, user, dto.getCategoryId(), dto.getAmount(),
                dto.getDescription(), dto.getType(), dto.getTransactionDate());
        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Long id) {

        User user = userService.findByEmail(userDetails.getUsername());
        transactionService.delete(id, user);

        return "redirect:/dashboard";
    }
}
