package com.lzl.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lzl.constant.MessageConstant;
import com.lzl.constant.StatusConstant;
import com.lzl.dto.CategoryDTO;
import com.lzl.dto.CategoryPageQueryDTO;
import com.lzl.entity.Category;
import com.lzl.exception.DeletionNotAllowedException;
import com.lzl.mapper.CategoryMapper;
import com.lzl.mapper.DishMapper;
import com.lzl.mapper.SetmealMapper;
import com.lzl.result.PageResult;
import com.lzl.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    /**
     * 新增分类
     * @param categoryDTO
     */
    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .status(StatusConstant.ENABLE)
                .build();
        BeanUtils.copyProperties(categoryDTO, category);

        categoryMapper.save(category);
    }

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult getPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        List<Category> categoryList = categoryMapper.getPage(categoryPageQueryDTO.getName(), categoryPageQueryDTO.getType());
        Page<Category> pageList = (Page<Category>) categoryList;
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }


    /**
     * 启用、禁用分类
     * @param status
     * @param id
     */
    @Override
    public void status(Integer status, Long id) {
        Category category = Category.builder()
                .status(status)
                .id(id)
                .build();
        categoryMapper.update(category);
    }


    /**
     * 修改分类
     * @param categoryDTO
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.update(category);
    }

    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        // 分类下如果有菜品不允许删除
        Integer dishCount = dishMapper.countByCategoryId(id);
        Integer setmealCount = setmealMapper.countByCategoryId(id);

        if (dishCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        if (setmealCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        categoryMapper.deleteById(id);
    }


    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }
}
