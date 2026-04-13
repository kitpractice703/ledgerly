package com.ledgerly.controller;

import com.ledgerly.dto.CategoryRequestDto;
import com.ledgerly.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String categoryPage(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "category/list";
    }

    @PostMapping
    public String save(@Valid CategoryRequestDto dto,
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("categories", categoryService.findAll());
            return "category/list";
        }

        try {
            categoryService.save(dto.getName(), dto.getType());
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("error", "카테고리 등록에 실패했습니다.");
            model.addAttribute("categories", categoryService.findAll());
            return "category/list";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam String type) {
        categoryService.update(id, name, type);
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/categories";
    }
}
