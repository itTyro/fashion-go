package com.lzl.mapper;

import com.lzl.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void saveBatch(List<DishFlavor> flavors);

    void delete(List<Long> ids);

    /**
     * 根据菜品id查询口味
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{id} ;")
    List<DishFlavor> getByDishId(Long id);
}
