package com.lzl.mapper;

import com.lzl.annotation.AutoFill;
import com.lzl.dto.DishPageQueryDTO;
import com.lzl.entity.Dish;
import com.lzl.enumeration.OperationType;
import com.lzl.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @Options(useGeneratedKeys = true, keyProperty = "id") // 返回主键值给实体
    @AutoFill(OperationType.INSERT)
    @Insert("insert into dish (name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) " +
            "values " +
            "(#{name},#{categoryId},#{price},#{image},#{description},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser});")
    void save(Dish dish);

    /**
     * 条件分页查询
     * @param dishPageQueryDTO
     * @return
     */
    List<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    Integer countEnableDishByIds(List<Long> ids);

    void delete(List<Long> ids);

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id} ;")
    Dish getById(Long id);

    /**
     * 动态修改菜品信息
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId} and status = 1;")
    List<DishVO> list(Integer categoryId);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
