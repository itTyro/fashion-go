package com.lzl.service;

import com.lzl.dto.ShoppingCartDTO;
import com.lzl.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void save(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> list();


    void sub(ShoppingCartDTO shoppingCartDTO);

    // 清空购物车
    void clear();
}
