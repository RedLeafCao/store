package com.can.store.shopping.commons.kizz.db;

import com.can.store.shopping.commons.kizz.lib.utils.Func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class DataObject {
    public Map<String, Object> data;

    public Object getObject(String name) {
        if (null == data) {
            return null;
        }
        return data.get(name);
    }

    public String getString(String name) {
        Object v = this.data.get(name);
        if (null == v) {
            return null;
        }
        return v.toString();
    }

    public String getStringDefault(String name, String default_v) {
        Object v = this.data.get(name);
        return Func.toStringDefault(v, default_v);
    }

    public Boolean getBoolean(String name) {
        Object v = this.data.get(name);
        return Func.toBoolean(v);
    }

    public boolean getBooleanDefault(String name, boolean default_v) {
        Object v = this.data.get(name);
        return Func.toBooleanDefault(v, default_v);
    }

    public Long getLong(String name) {
        Object v = this.data.get(name);
        return Func.toLong(v);
    }

    public long getLongDefault(String name, long default_v) {
        Object v = this.data.get(name);
        return Func.toLongDefault(v, default_v);
    }

    public Double getDouble(String name) {
        Object v = this.data.get(name);
        return Func.toDouble(v);
    }

    public double getDoubleDefault(String name, double default_v) {
        Object v = this.data.get(name);
        return Func.toDoubleDefault(v, default_v);
    }

    public Integer getInt(String name) {
        return getInteger(name);
    }

    public Integer getInteger(String name) {
        Object v = this.data.get(name);
        return Func.toInteger(v);
    }

    public int getIntegerDefault(String name, int default_v) {
        Object v = this.data.get(name);
        return Func.toIntegerDefault(v, default_v);
    }

    public void put(String name, Object value) {
        if (null == this.data) {
            this.data = new HashMap<>();
        }
        this.data.put(name, value);
    }

    public static DataObject getInstance() {
        DataObject obj = new DataObject();
        obj.data = new HashMap<>();
        return obj;
    }

    public static DataObject getInstance(Map<String, Object> data) {
        DataObject obj = new DataObject();
        obj.data.putAll(data);
        return obj;
    }

    public static List<Map<String, Object>> getDataList(List<DataObject> obj_list) {
        if (null == obj_list) {
            return null;
        }
        List<Map<String, Object>> datalist = new ArrayList<>();
        for (int i = 0; i < obj_list.size(); i++) {
            Map<String, Object> row_t = new HashMap<>();
            row_t.putAll(obj_list.get(i).data);//阻断引用的影响
            datalist.add(row_t);
        }
        return datalist;
    }

    public static List<Map<String, Object>> getDataList(List<DataObject> obj_list, String fieldlist) {
        if (null == obj_list) {
            return null;
        }
        String[] fields = fieldlist.split(",");
        List<Map<String, Object>> datalist = new ArrayList<>();
        for (int i = 0; i < obj_list.size(); i++) {
            Map<String, Object> row = new HashMap<>();
            for (int j = 0; j < fields.length; j++) {
                String k = fields[j];
                row.put(k, obj_list.get(i).data.get(k));
            }
            datalist.add(row);
        }
        return datalist;
    }

    public static List<Map<String, Object>> getDataList(List<DataObject> obj_list, Map<String, String> field2alias) {
        if (null == obj_list) {
            return null;
        }
        List<Map<String, Object>> datalist = new ArrayList<>();
        for (int i = 0; i < obj_list.size(); i++) {
            Map<String, Object> row = new HashMap<>();
            for (String f : field2alias.keySet()) {
                String k = field2alias.get(f);
                row.put(k, obj_list.get(i).data.get(k));
            }
            datalist.add(row);
        }
        return datalist;
    }
}