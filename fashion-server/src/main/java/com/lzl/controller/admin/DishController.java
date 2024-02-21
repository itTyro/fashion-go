package com.lzl.controller.admin;

import com.lzl.dto.DishDTO;
import com.lzl.dto.DishPageQueryDTO;
import com.lzl.result.PageResult;
import com.lzl.result.Result;
import com.lzl.service.DishService;
import com.lzl.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
public class DishController {

    @Autowired
    private DishService dishService;

    @ApiOperation("新增菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.save(dishDTO);
        return Result.success();
    }


    @ApiOperation("菜品分页")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    @ApiOperation("菜品删除")
    @DeleteMapping
    public Result deleteDish(@RequestParam List<Long> ids) {  // url ?后面拼接的，默认可以使用数组接受，使用list加 @requestParam
        log.info("菜品删除：{}", ids);
        dishService.deleteDish(ids);
        return Result.success();
    }

    @ApiOperation("根据ID查询")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据ID查询菜品：{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }


    @ApiOperation("菜品修改")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("菜品修改：{}", dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }


    @ApiOperation("菜品启售停售")
    @PostMapping("/status/{status}")
    public Result statusDish(@PathVariable Integer status, Long id) {
        log.info("菜品启售或停售：{}, id:{}", status, id);
        dishService.status(status, id);
        return Result.success();
    }

    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> list(@RequestParam Integer categoryId) {
        log.info("根据分类id查询菜品: {}", categoryId);
        List<DishVO> dishVOList = dishService.list(categoryId);
        return Result.success(dishVOList);
    }
}
