package com.lzl.service;

import com.lzl.dto.SetmealDTO;
import com.lzl.dto.SetmealPageQueryDTO;
import com.lzl.result.PageResult;
import com.lzl.vo.DishItemVO;
import com.lzl.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void save(SetmealDTO setmealDTO);

    PageResult getPage(SetmealPageQueryDTO setmealPageQueryDTO);

    void delete(List<Long> ids);

    SetmealVO getById(Long id);

    void update(SetmealDTO setmealDTO);

    void status(Integer status, Long id);

    List<SetmealVO> list(Integer categoryId);

    List<DishItemVO> getDishItems(Long setmealId);
}
