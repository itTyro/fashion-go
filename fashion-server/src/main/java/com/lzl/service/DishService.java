package com.lzl.service;

import com.lzl.dto.DishDTO;
import com.lzl.dto.DishPageQueryDTO;
import com.lzl.result.PageResult;
import com.lzl.vo.DishVO;

import java.util.List;

public interface DishService {
    void save(DishDTO dishDTO);

    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    void deleteDish(List<Long> ids);

    DishVO getById(Long id);

    void update(DishDTO dishDTO);

    void status(Integer status, Long id);

    List<DishVO> list(Integer categoryId);
}
