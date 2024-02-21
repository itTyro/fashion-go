package com.lzl.controller.user;

import com.lzl.result.Result;
import com.lzl.service.SetmealService;
import com.lzl.vo.DishItemVO;
import com.lzl.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "分类套餐管理")
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;


    @ApiOperation("根据分类id查询套餐")
    @GetMapping("/list")
    public Result<List<SetmealVO>> list(Integer categoryId) {
        log.info("根据分类id查询套餐：{}", categoryId);
        List<SetmealVO> setmealVOList = setmealService.list(categoryId);
        return Result.success(setmealVOList);
    }


    @ApiOperation("根据套餐id查询包含菜品")
    @GetMapping("/dish/{setmealId}")
    public Result<List<DishItemVO>> dishList(@PathVariable Long setmealId) {
        log.info("根据套餐id查询包含菜品: {}", setmealId);

        List<DishItemVO> dishItemVOList = setmealService.getDishItems(setmealId);
        return Result.success(dishItemVOList);
    }
}
