package com.lzl.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lzl.constant.MessageConstant;
import com.lzl.constant.StatusConstant;
import com.lzl.dto.DishDTO;
import com.lzl.dto.DishPageQueryDTO;
import com.lzl.entity.Dish;
import com.lzl.entity.DishFlavor;
import com.lzl.exception.DeletionNotAllowedException;
import com.lzl.mapper.*;
import com.lzl.result.PageResult;
import com.lzl.service.DishService;
import com.lzl.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class DishServiceImpl implements DishService {

    private final static String DISH_BY_CATEGORY_ID = "cache:dishByCategoryId:";

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO
     */
    @Transactional
    @Override
    public void save(DishDTO dishDTO) {
        Dish dish = Dish.builder()
                .status(StatusConstant.DISABLE)
                .build();
        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.save(dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 为dishId赋值
        flavors.forEach(flavor -> flavor.setDishId(dish.getId()));

        dishFlavorMapper.saveBatch(flavors);

        clearCache(dishDTO.getCategoryId().toString());
    }


    /**
     * 分页条件查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        List<DishVO> dishVOList = dishMapper.page(dishPageQueryDTO);
        Page<DishVO> pageList = (Page<DishVO>) dishVOList;
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }


    /**
     * 菜品删除
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteDish(List<Long> ids) {
        // 1. 判断当前菜品是否是起售状态
        Integer count = dishMapper.countEnableDishByIds(ids);
        if (count > 0) {
            // 删除的菜品中有启售状态的
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        // 2. 删除的菜品是否关联着套餐
        count = setmealDishMapper.getCategoryByDishIds(ids);
        if (count > 0) {
            // 关联着套餐
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 3. 删除菜品
        dishMapper.delete(ids);
        // 4. 删除口味
        dishFlavorMapper.delete(ids);

        clearCache("*");
    }

    /**
     * 根据ID查询菜品信息
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        // 1. 查询菜品信息
        Dish dish = dishMapper.getById(id);

        // 2. 查询口味信息
        List<DishFlavor> dishFlavorList = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavorList);

        return dishVO;
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 1. 修改菜品信息
        dishMapper.update(dish);

        // 2. 删除菜品口味信息
        dishFlavorMapper.delete(Collections.singletonList(dishDTO.getId()));

        // 3. 新增菜品口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
        dishFlavorMapper.saveBatch(flavors);

        clearCache("*");
    }

    /**
     * 菜品启售或停售
     *
     * @param status
     */
    @Override
    public void status(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.update(dish);
        clearCache("*");
    }

    @Override
    public List<DishVO> list(Integer categoryId) {
        //查询缓存
        List<DishVO> dishVoList = (List<DishVO>) redisTemplate.opsForValue().get(DISH_BY_CATEGORY_ID + categoryId);
        if (dishVoList != null) {
            return dishVoList;
        }

        dishVoList = dishMapper.list(categoryId);
        dishVoList.forEach(dishVO -> dishVO.setFlavors(dishFlavorMapper.getByDishId(dishVO.getId())));

        redisTemplate.opsForValue().set(DISH_BY_CATEGORY_ID + categoryId, dishVoList);
        return dishVoList;
    }

    public void clearCache(String suffix) {
        Set<String> keys = redisTemplate.keys(DISH_BY_CATEGORY_ID + suffix);
        redisTemplate.delete(keys);
    }
}
