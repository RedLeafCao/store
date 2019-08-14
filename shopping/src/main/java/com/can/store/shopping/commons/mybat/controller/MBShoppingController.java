package com.can.store.shopping.commons.mybat.controller;

import com.can.store.shopping.commons.kiss.helper.session.UserSession;
import com.can.store.shopping.commons.kizz.http.response.Response;
import com.can.store.shopping.commons.mybat.mapper.CartMapper;
import com.can.store.shopping.commons.mybat.model.Cart;
import com.can.store.shopping.commons.mybat.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mybatis实现的购物controller
 * 2019.08.14
 */
@Controller
@Api(tags = "购物主要流程",description = "添加购物车，生成订单，订单支付等操作")
@RequestMapping("/mb/shop")
public class MBShoppingController {
    @Autowired
    private CartService cartService;

    @ApiOperation("添加购物车")
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public Response addCart(
            @RequestBody Cart cart
    ){
        UserSession us = UserSession.getInstance();
        Long userId = us.getUserId();
        if(null == userId || 0L == userId){
            return Response.failed(600,600,"用户已退出，请登录");
        }
        if(!userId.equals(cart.getUserId())){
            return Response.failed(601,601,"用户名不一致，请重新登录");
        }
        // TODO:判断库存量

        // 判断购物车中是否有该商品
        Cart cart1 = cartService.selectGoods(cart);
        if (null == cart1){
            int raw = cartService.insertGood(cart);
            if(raw < 1){
                return Response.failed(601,601,"商品添加失败");
            }
        }else {
            cart.setQuantity(cart1.getQuantity()+cart.getQuantity());
            int raw = cartService.updateQuantity(cart);
            if(raw < 1){
                return Response.failed(602,602,"商品添加失败");
            }
        }
        return Response.success("商品添加成功");
    }

    @ApiOperation("查看当前用户购物车")
    @RequestMapping(value = "list_cart",method = RequestMethod.POST)
    @ResponseBody
    public Response listCart(){
        UserSession us = UserSession.getInstance();
        Long userId = us.getUserId();
        if(null == userId || 0L == userId){
            return Response.failed(600,600,"当前用户已退出，请重新登录");
        }
        List<Cart> carts = cartService.selectCartByUserId(userId);
        if(carts.size() < 1){
            return Response.failed(601,601,"购物车为空");
        }
        List<Map<String,Object>> cMap = new ArrayList<>();
        int i = 0;
        for(Cart cart : carts){
            Map<String,Object> map = new HashMap<>();
            map.put("id",cart.getId());
            map.put("user_id",cart.getUserId());
            map.put("goods_id",cart.getGoodId());
            map.put("quantity",cart.getQuantity());
            map.put("is_chosen",cart.getIsChosen());
            cMap.add(i,map);
            i++;
        }
        return Response.success(cMap);
    }

    @ApiOperation("商品添加订单")
    @RequestMapping(value = "/add_order",method = RequestMethod.POST)
    @ResponseBody
    public Response addOrder(
            @RequestBody Cart cart
    ){
        UserSession us = UserSession.getInstance();
        Long userId = us.getUserId();
        if(0L == userId || null == userId){
            return Response.failed(600,600,"当前用户已退出，请重新登录");
        }
        if(!userId.equals(cart.getUserId())){
            return Response.failed(601,601,"用户名不一致，请重新登录");
        }
        if(null == cart.getQuantity() || 0 == cart.getQuantity()){
            return Response.failed(602,602,"商品数量为零，不可添加");
        }
        int raw = cartService.updateGoodIsChosen(cart);
        if(raw < 1){
            return Response.failed(603,603,"商品添加订单失败");
        }
        return Response.success("商品添加成功");
    }
}
