package com.lzl.controller.admin;

import com.lzl.dto.CategoryDTO;
import com.lzl.dto.CategoryPageQueryDTO;
import com.lzl.entity.Category;
import com.lzl.result.PageResult;
import com.lzl.result.Result;
import com.lzl.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类管理")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param categoryDTO
     */
    @ApiOperation("分类添加")
    @PostMapping
    public Result save(@RequestBody CategoryDTO categoryDTO) {
        log.info("套餐添加操作：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 条件分页查询
     *
     */
    @ApiOperation("分类分页查询")
    @GetMapping("/page")
    public Result<PageResult> getPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类分页条件查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.getPage(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用/禁用分类
     *
     */
    @ApiOperation("分类启用/禁用")
    @PostMapping("/status/{status}")
    public Result status(@PathVariable Integer status, Long id) {
        log.info("分类启用/禁用：{}，id: {}", status, id);
        categoryService.status(status, id);
        return Result.success();
    }

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO){
        log.info("分类修改：{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result<String> deleteById(Long id){
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

}
