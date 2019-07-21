package com.can.store.shopping.commons.kizz.db.mysql;

import java.util.HashMap;
import java.util.Map;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class LeftJoinObject {
    public String tablename;
    public String aliasname;
    public String mField;
    public String sField;
    public Map<String, String> mField2sField;

    public static LeftJoinObject getInstance(String table, String alias, String mField, String sField) {
        LeftJoinObject obj = new LeftJoinObject();
        obj.tablename = table;
        obj.aliasname = alias;
        obj.mField = mField;
        obj.sField = sField;
        obj.mField2sField = new HashMap<>();
        return obj;
    }
}
