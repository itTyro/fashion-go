package com.lzl.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lzl.constant.MessageConstant;
import com.lzl.constant.StatusConstant;
import com.lzl.dto.SetmealDTO;
import com.lzl.dto.SetmealPageQueryDTO;
import com.lzl.entity.Dish;
import com.lzl.entity.Setmeal;
import com.lzl.entity.SetmealDish;
import com.lzl.exception.DataIsNullException;
import com.lzl.exception.DeletionNotAllowedException;
import com.lzl.mapper.DishMapper;
import com.lzl.mapper.SetmealDishMapper;
import com.lzl.mapper.SetmealMapper;
import com.lzl.result.PageResult;
import com.lzl.service.SetmealService;
import com.lzl.vo.DishItemVO;
import com.lzl.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    @CacheEvict(cacheNames = "cache setmeal", key = "#a0.categoryId")
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = Setmeal.builder()
                .status(StatusConstant.DISABLE)
                .build();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 1. 添加套餐
        setmealMapper.save(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 设置套餐id
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));

        // 2. 添加套餐下的菜品
        setmealDishMapper.setmealByDish(setmealDishes);
    }

    @Override
    public PageResult getPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        List<Setmeal> setmealList = setmealMapper.getPage(setmealPageQueryDTO);
        Page<Setmeal> setmealPage = (Page<Setmeal>) setmealList;
        return new PageResult(setmealPage.getTotal(), setmealPage.getResult());
    }

    /**
     * 批量删除套餐
     *
     * @param ids
     */
    @CacheEvict(cacheNames = "cache setmeal", allEntries = true)
    @Override
    @Transactional
    public void delete(List<Long> ids) {
        // 1. 判断当前套餐是否在售
        Integer count = setmealMapper.getEnableCount(ids);
        if (count > 0) {
            // 有套餐在售
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }

        // 2. 删除套餐
        setmealMapper.delete(ids);

        // 3. 删除套餐下的菜品
        setmealDishMapper.delete(ids);
    }

    /**
     * 根据id查询套餐信息
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        // 1. 查询套餐信息
        Setmeal setmeal = setmealMapper.getByid(id);

        // 2. 查询套餐下菜品信息
        List<SetmealDish> setmealDishList = setmealDishMapper.getBySetmealId(id);

        SetmealVO setmealVO = new SetmealVO();
        if (ObjectUtils.isEmpty(setmeal)) {
            throw new DataIsNullException("查询的数据不存在");
        }
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishList);

        // 3. 封装返回
        return setmealVO;
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    @CacheEvict(cacheNames = "cache setmeal", allEntries = true)
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 1. 修改套餐信息
        setmealMapper.update(setmeal);

        // 2. 删除套餐下的菜品
        setmealDishMapper.delete(Collections.singletonList(setmealDTO.getId()));
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDTO.getId()));

        // 3. 添加套餐下的菜品
        setmealDishMapper.setmealByDish(setmealDishes);
    }

    /**
     * 修改套餐启售/仅售
     *
     * @param status
     * @param id
     */
    @CacheEvict(cacheNames = "cache setmeal", allEntries = true)
    @Override
    public void status(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();

        setmealMapper.update(setmeal);
    }

    /**
     * 根据分类id查询套餐
     *
     * @param categoryId
     * @return
     */
    @Cacheable(cacheNames = "cache setmeal", key = "#a0")
    @Override
    public List<SetmealVO> list(Integer categoryId) {
        List<SetmealVO> setmealVOList = setmealMapper.getByCateGoryId(categoryId);
        return setmealVOList;
    }

    /**
     * 根据套餐id查询包含的菜品
     *
     * @param setmealId
     * @return
     */
    @Override
    public List<DishItemVO> getDishItems(Long setmealId) {
        // 1. 查询套餐关联的菜品表
        List<SetmealDish> setmealDishList = setmealDishMapper.getBySetmealId(setmealId);

        List<DishItemVO> dishItemVOList = new ArrayList<>();

        // 2. 根据查询出来的菜品查询菜品表
        setmealDishList.forEach(setmealDish -> {
            DishItemVO dishItemVO = new DishItemVO();

            // 3. 封装所需信息
            BeanUtils.copyProperties(setmealDish, dishItemVO);
            Dish dish = dishMapper.getById(setmealDish.getDishId());
            BeanUtils.copyProperties(dish, dishItemVO);
            dishItemVOList.add(dishItemVO);
        });


        return dishItemVOList;
    }
}
