package com.can.store.shopping.commons.kizz.db.mysql;

import java.util.Collection;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class WhereClause {
    /**
     * 和前一个where的连接符 true:and, false:or
     */
    public boolean clause = true;

    /**
     * 条件作用的字段
     */
    public String field = "";

    /**
     * 条件值/值列表，或者'is null'、'is not null'
     */
    public Object filter = "";

    /**
     * 条件值为表达式时，表达式操作符号，'=','>','<','<>','like','in', 'not in'
     */
    public String operator = "";

    /**
     * 表达式的操作字段。默认为无。可以指定同一数据表其他字段作为操作对象
     * <p>
     * where field1=field2
     * where field1>field2
     */
    public String exp_field = "";

    /**
     * operator为in或not in时使用
     */
    public Collection filterSet;

    /**
     * 子查询作为条件
     */
    public String subQuery;

    public static WhereClause getInsance() {
        WhereClause where = new WhereClause();
        where.clause = true;
        return where;
    }

    public static WhereClause getInsance(String field) {
        WhereClause where = new WhereClause();
        where.clause = true;
        where.operator = "=";
        where.field = field;
        return where;
    }
}