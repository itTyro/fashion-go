package com.lzl.service.impl;

import com.lzl.context.BaseContext;
import com.lzl.dto.ShoppingCartDTO;
import com.lzl.entity.Dish;
import com.lzl.entity.Setmeal;
import com.lzl.entity.ShoppingCart;
import com.lzl.mapper.DishMapper;
import com.lzl.mapper.SetmealMapper;
import com.lzl.mapper.ShoppingCartMapper;
import com.lzl.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void save(ShoppingCartDTO shoppingCartDTO) {

        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(currentId);

        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        // 查询数据库
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        // 查询到数据数量加1
        if (!CollectionUtils.isEmpty(shoppingCartList)) {
            ShoppingCart cart = shoppingCartList.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.update(cart);
        } else {

            // 没有查询到数据保存数据
            Long dishId = shoppingCart.getDishId();
            if (dishId != null) {
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                Long setmealId = shoppingCart.getSetmealId();
                Setmeal setmeal = setmealMapper.getByid(setmealId);
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
            }

            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            // 保存数据
            shoppingCartMapper.save(shoppingCart);

        }


    }

    /**
     * 查看购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(currentId);

        return shoppingCartMapper.list(shoppingCart);
    }


    /**
     * 删除购物车中的一个商品
     *
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        // 如果数量不是1，则减少数量，剩余1的时候直接删除
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(currentId);
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        // 查询数据
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (CollectionUtils.isEmpty(list)) {
            // 没有该数据结束
            return;
        }

        ShoppingCart cart = list.get(0);
        Integer number = cart.getNumber();

        // 数量大于1，减少
        if (number > 1) {
            cart.setNumber(number - 1);
            shoppingCartMapper.update(cart);
        } else
            // 数量等于1，直接删除该信息
            shoppingCartMapper.deleteById(cart);

    }

    @Override
    public void clear() {
        Long currentId = BaseContext.getCurrentId();
        shoppingCartMapper.clear(currentId);
    }
}
