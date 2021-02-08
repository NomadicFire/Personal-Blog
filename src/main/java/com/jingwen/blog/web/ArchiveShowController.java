package com.jingwen.blog.web;

import com.jingwen.blog.dao.BlogRepository;
import com.jingwen.blog.service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArchiveShowController {

    @Autowired
    ManagementService managementService;

    @GetMapping("/archive")
    public String archives(Model model) {
        model.addAttribute("archiveMap",managementService.archiveBlog());
        model.addAttribute("blogCount", managementService.countBlog());
        return "/archive";
    }
}
