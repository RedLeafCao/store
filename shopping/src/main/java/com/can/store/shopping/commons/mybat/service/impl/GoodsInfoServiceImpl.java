package com.can.store.shopping.commons.mybat.service.impl;

import com.can.store.shopping.commons.mybat.mapper.GoodsInfoMapper;
import com.can.store.shopping.commons.mybat.model.GoodsInfo;
import com.can.store.shopping.commons.mybat.service.GoodsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品详细信息增删查改
 * 2019.08.14
 */
@Service(value = "goodsInfoServiceImpl")
public class GoodsInfoServiceImpl implements GoodsInfoService {
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;

    @Override
    public int insertGood(GoodsInfo goodsInfo) {
        return goodsInfoMapper.insertGood(goodsInfo);
    }

    @Override
    public int updateGoodInfo(GoodsInfo goodsInfo) {
        return goodsInfoMapper.updateGoodInfo(goodsInfo);
    }

    @Override
    public int deleteGood(Integer id) {
        return goodsInfoMapper.deleteGood(id);
    }

    @Override
    public List<GoodsInfo> selectAll() {
        return goodsInfoMapper.selectAll();
    }

    @Override
    public GoodsInfo selectOne(Integer id) {
        return goodsInfoMapper.selectOne(id);
    }

    @Override
    public List<GoodsInfo> selectByGoodName(String goodsName) {
        return goodsInfoMapper.selectByGoodName(goodsName);
    }

    @Override
    public List<GoodsInfo> selectByProviderId(String str) {
        return goodsInfoMapper.selectByProviderId(str);
    }

    @Override
    public List<GoodsInfo> selectByCategoryId(String str) {
        return goodsInfoMapper.selectByCategoryId(str);
    }

    @Override
    public List<GoodsInfo> selectByCategoryId1(String str) {
        return goodsInfoMapper.selectByCategoryId1(str);
    }

    @Override
    public List<GoodsInfo> selectByCategoryId2(String str) {
        return goodsInfoMapper.selectByCategoryId2(str);
    }

    @Override
    public List<GoodsInfo> selectByIsVirtual(Integer isVirtual, String str) {
        return goodsInfoMapper.selectByIsVirtual(isVirtual,str);
    }

    @Override
    public List<GoodsInfo> selectByBrandId(String str) {
        return goodsInfoMapper.selectByBrandId(str);
    }
}
