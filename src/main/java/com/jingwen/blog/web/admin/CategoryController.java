package com.jingwen.blog.web.admin;

import com.jingwen.blog.po.Category;
import com.jingwen.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String categories(@PageableDefault(size = 8, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        model.addAttribute("page", categoryService.listCategory(pageable));
        return "admin/categories";
    }

    @GetMapping("/categories/input")
    public String input(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories-input";
    }

    @GetMapping("/categories/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategory(id));
        return "admin/categories-input";
    }

    @PostMapping("/categories")
    public String post(@Valid Category category, BindingResult result, RedirectAttributes attributes) {
        Category category1 = categoryService.getCategoryByName(category.getName());
        if (category1 != null) {
            result.rejectValue("name", "nameError", "Cannot add same category");
        }
        if (result.hasErrors()) {
            return "admin/categories-input";
        }
        Category c = categoryService.saveCategory(category);
        if (c == null) {
            attributes.addFlashAttribute("message", "Add new category failed!");
        } else {
            attributes.addFlashAttribute("message", "Add new category succeed!");
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}")
    public String editPost(@Valid Category category, BindingResult result, @PathVariable Long id, RedirectAttributes attributes) {
        Category category1 = categoryService.getCategoryByName(category.getName());
        if (category1 != null) {
            result.rejectValue("name", "nameError", "Cannot add same category");
        }
        if (result.hasErrors()) {
            return "admin/categories-input";
        }
        Category c = categoryService.updateCategory(id, category);
        if (c == null) {
            attributes.addFlashAttribute("message", "Update failed!");
        } else {
            attributes.addFlashAttribute("message", "Update succeed!");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        categoryService.deleteCategory(id);
        attributes.addFlashAttribute("message", "Delete succeed!");
        return "redirect:/admin/categories";
    }
}
