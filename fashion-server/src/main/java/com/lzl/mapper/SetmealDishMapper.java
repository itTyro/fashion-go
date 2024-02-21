package com.lzl.mapper;

import com.lzl.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    Integer getCategoryByDishIds(List<Long> ids);

    /**
     * 添加套餐下的菜品
     * @param setmealDishes
     */

    void setmealByDish(List<SetmealDish> setmealDishes);

    /**
     * 删除套餐的同时删除菜品
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 根据套餐id查询菜品
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id};")
    List<SetmealDish> getBySetmealId(Long id);
}
