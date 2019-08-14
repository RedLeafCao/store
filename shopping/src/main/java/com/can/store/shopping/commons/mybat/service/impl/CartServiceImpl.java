package com.can.store.shopping.commons.mybat.service.impl;

import com.can.store.shopping.commons.mybat.mapper.CartMapper;
import com.can.store.shopping.commons.mybat.model.Cart;
import com.can.store.shopping.commons.mybat.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车service的实现
 * 2019.08.14
 */
@Service(value = "cartService")
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;

    @Override
    public List<Cart> selectCartByUserId(Long userId) {
        return cartMapper.selectCartByUserId(userId);
    }

    @Override
    public List<Cart> selectCartIsChosen(Long userId) {
        return cartMapper.selectCartIsChosen(userId);
    }

    @Override
    public Cart selectGoods(Cart cart) {
        return cartMapper.selectGoods(cart);
    }

    @Override
    public int updateGoodIsChosen(Cart cart) {
        return cartMapper.updateGoodIsChosen(cart);
    }

    @Override
    public int updateQuantity(Cart cart) {
        return cartMapper.updateQuantity(cart);
    }

    @Override
    public int insertGood(Cart cart) {
        return cartMapper.insertGood(cart);
    }

    @Override
    public int deleteGood(Cart cart) {
        return cartMapper.deleteGood(cart);
    }
}
