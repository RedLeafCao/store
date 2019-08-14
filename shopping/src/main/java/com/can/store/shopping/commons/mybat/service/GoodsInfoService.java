package com.can.store.shopping.commons.mybat.service;

import com.can.store.shopping.commons.mybat.model.GoodsInfo;

import java.util.List;

/**
 * 商品详细信息
 * 2019.08.14
 */
public interface GoodsInfoService {
    // 添加商品
    int insertGood(GoodsInfo goodsInfo);
    // 修改商品信息
    int updateGoodInfo(GoodsInfo goodsInfo);
    // 删除商品
    int deleteGood(Integer id);
    // 查询所有商品
    List<GoodsInfo> selectAll();
    // 查询指定商品
    GoodsInfo selectOne(Integer id);
    // 按商品名查询
    List<GoodsInfo> selectByGoodName(String goodsName);
    // 按商品供应商查询,连表goods_provider，从goods_provider表中查询到供应商名like字符串str的provider_id
    List<GoodsInfo> selectByProviderId(String str);
    // 按商品的一级分类查询，连接表goods_category level = 0
    List<GoodsInfo> selectByCategoryId(String str);
    // 按商品的二级分类查询，连接表goods_category level 1
    List<GoodsInfo> selectByCategoryId1(String str);
    // 按商品的三级分类查询，连接表goods_category level = 2
    List<GoodsInfo> selectByCategoryId2(String str);
    // 按商品是否为虚拟产品
    List<GoodsInfo> selectByIsVirtual(Integer isVirtual,String str);
    // 按商品的品牌名查询，连接表goods_brand
    List<GoodsInfo> selectByBrandId(String str);
    // TODO：其他查询操作省略，待完善
}
