package com.lzl.mapper;

import com.lzl.annotation.AutoFill;
import com.lzl.dto.SetmealPageQueryDTO;
import com.lzl.entity.Setmeal;
import com.lzl.enumeration.OperationType;
import com.lzl.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);


    /**
     * 新增套餐
     * @param setmeal
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoFill(OperationType.INSERT)
    @Insert("insert into setmeal (category_id, name, price, description, image, create_time, update_time, create_user, update_user) " +
            "values" +
            " (#{categoryId}, #{name},#{price},#{description},#{image},#{createTime},#{updateTime},#{createUser},#{updateUser});")
    void save(Setmeal setmeal);

    /**
     * 套餐动态条件查询
     * @param setmealPageQueryDTO
     * @return
     */
    List<Setmeal> getPage(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 查询删除的id在售的数量
     * @param ids
     * @return
     */
    Integer getEnableCount(List<Long> ids);

    /**
     * 删除套餐
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id} ;")
    Setmeal getByid(Long id);

    /**
     * 动态修改套餐信息
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @Select("select * from setmeal where category_id = #{categoryId} and status = 1;")
    List<SetmealVO> getByCateGoryId(Integer categoryId);


    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
