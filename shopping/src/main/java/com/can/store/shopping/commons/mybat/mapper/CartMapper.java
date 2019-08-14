package com.can.store.shopping.commons.mybat.mapper;

import com.alipay.api.domain.Car;
import com.can.store.shopping.commons.mybat.model.Cart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单查询，添加，删除，修改mapper
 * 2019.08.14
 */
@Mapper
public interface CartMapper {
    // 查询当前用户购物车的所有已添加商品
    List<Cart> selectCartByUserId(Long userId);
    // 查询当前用户购物车被选中的所有商品详情
    List<Cart> selectCartIsChosen(Long userId);
    // 查询当前用户购物车中的某一件商品
    Cart selectGoods(Cart cart);
    // 修改已有商品状态，添加订单前提
    int updateGoodIsChosen(Cart cart);
    // 修改已有商品的数量
    int updateQuantity(Cart cart);
    // 添加商品进购物车
    int insertGood(Cart cart);
    // 删除购物车
    int deleteGood(Cart cart);
}
