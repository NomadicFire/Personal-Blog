package com.jingwen.blog.dao;

import com.jingwen.blog.po.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String name);

    @Query("select c from Category c")
    List<Category> findTop(Pageable pageable);
}
