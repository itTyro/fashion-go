package com.lzl.controller.user;

import com.lzl.entity.Category;
import com.lzl.result.Result;
import com.lzl.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "用户端分类管理")
@RestController("userCategoryController")
@RequestMapping("/user/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("分类条件查询")
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type) {
        log.info("根据条件查询分类：{}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
