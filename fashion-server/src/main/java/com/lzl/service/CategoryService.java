package com.lzl.service;

import com.lzl.dto.CategoryDTO;
import com.lzl.dto.CategoryPageQueryDTO;
import com.lzl.entity.Category;
import com.lzl.result.PageResult;

import java.util.List;

public interface CategoryService {
    void save(CategoryDTO categoryDTO);

    PageResult getPage(CategoryPageQueryDTO categoryPageQueryDTO);

    void status(Integer status, Long id);

    void update(CategoryDTO categoryDTO);

    void deleteById(Long id);

    List<Category> list(Integer type);
}
