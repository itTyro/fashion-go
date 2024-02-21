package com.lzl.mapper;

import com.lzl.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {



    /**
     * 修改数据
     * @param shoppingCart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id};")
    void update(ShoppingCart shoppingCart);

    /**
     * 添加购物车
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) values " +
            "(#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime});")
    void save(ShoppingCart shoppingCart);

    /**
     * 查询数据
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 删除商品
     *
     * @param cart
     */
    @Delete("delete from shopping_cart where id = #{id};")
    void deleteById(ShoppingCart cart);

    @Delete("delete from shopping_cart where user_id = #{userId};")
    void clear(Long userId);

    /**
     * 批量加入购物车
     * @param shoppingCartList
     */
    void saveBatch(List<ShoppingCart> shoppingCartList);
}
