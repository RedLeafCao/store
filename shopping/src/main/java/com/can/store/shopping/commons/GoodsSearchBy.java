package com.can.store.shopping.commons;

import com.sun.xml.internal.bind.v2.TODO;

public enum GoodsSearchBy {
    // 应该有更多的查询方式，待列出
    // 单条件查询
    ALL(0,null),
    ONE(1,"id"),
    PROVIDER(2,"provier_id"),
    // 双条件查询
    CATEGORYID(3,"category_id"),
    CATEGORYID1(4,"category_id_1"),
    CATEGORYID2(5,"category_id_2"),
    BRANDID(6,"brand_id"),
    NAME(7,"name"),
    ISVIRTUAL(8,"is_virtual");
    // 多条件组合查询
    // TODO:

    // 查询类型
    private int searchId;
    // 查询条件
    private String condition;
    private GoodsSearchBy(int searchId,String condition){
        this.searchId = searchId;
        this.condition = condition;
    }

    public int getSearchId() {
        return searchId;
    }

    public String getCondition() {
        return condition;
    }
}
