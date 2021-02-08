package com.jingwen.blog.web;

import com.jingwen.blog.po.Category;
import com.jingwen.blog.service.CategoryService;
import com.jingwen.blog.service.ManagementService;
import com.jingwen.blog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CategoryShowController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ManagementService managementService;

    @GetMapping("/categories/{id}")
    public String categories(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable, @PathVariable Long id, Model model) {
        List<Category> categories = categoryService.listCategoryTop(10000);
        if (id == -1) {
            id = categories.get(0).getId();
        }
        BlogQuery blogQuery = new BlogQuery();
        blogQuery.setCategoryId(id);
        model.addAttribute("categories", categories);
        model.addAttribute("page", managementService.listBlog(pageable, blogQuery));
        model.addAttribute("activeCategoryId", id);
        return "categories";
    }
}
