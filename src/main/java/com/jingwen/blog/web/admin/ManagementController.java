package com.jingwen.blog.web.admin;

import com.jingwen.blog.po.Blog;
import com.jingwen.blog.po.User;
import com.jingwen.blog.service.CategoryService;
import com.jingwen.blog.service.ManagementService;
import com.jingwen.blog.service.TagService;
import com.jingwen.blog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class ManagementController {

    private static final String PUBLISH = "admin/publish";
    private static final String MANAGEMENT = "admin/management";
    private static final String REDIRECT_LIST = "redirect:/admin/management";

    @Autowired
    private ManagementService managementService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @GetMapping("/management")
    public String management(@PageableDefault(size = 6, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable, BlogQuery blog, Model model) {
        model.addAttribute("categories", categoryService.listCategory());
        model.addAttribute("page", managementService.listBlog(pageable, blog));
        return MANAGEMENT;
    }

    @PostMapping("/management/search")
    public String search(@PageableDefault(size = 6, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable, BlogQuery blog, Model model) {
        model.addAttribute("page", managementService.listBlog(pageable, blog));
        return "admin/management :: blogList";
    }

    @GetMapping("/management/input")
    public String publish(Model model) {
        setCategoryAndTag(model);
        model.addAttribute("blog", new Blog());
        return PUBLISH;
    }

    private void setCategoryAndTag(Model model) {
        model.addAttribute("categories", categoryService.listCategory());
        model.addAttribute("tags", tagService.listTag());
    }

    @GetMapping("/management/{id}/input")
    public String editPublish(@PathVariable Long id, Model model) {
        setCategoryAndTag(model);
        Blog blog = managementService.getBlog(id);
        blog.init();
        model.addAttribute("blog", blog);
        return PUBLISH;
    }

    @PostMapping("/management")
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session) {
        blog.setUser((User) session.getAttribute("user"));
        blog.setCategory(categoryService.getCategory(blog.getCategory().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));
        Blog b;
        if (blog.getId() == null) {
            b = managementService.saveBlog(blog);
        } else {
            b = managementService.updateBlog(blog.getId(), blog);
        }
        if(b == null) {
            attributes.addFlashAttribute("message", "Operation Failed");
        } else {
            attributes.addFlashAttribute("message", "Operation Succeed");
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/management/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        managementService.deleteBlog(id);
        attributes.addFlashAttribute("message", "Delete Succeed!");
        return REDIRECT_LIST;
    }
}
